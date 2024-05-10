package com.dokja.mizumi.presentation.book

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dokja.mizumi.R
import com.dokja.mizumi.data.local.tracker.Track
import com.dokja.mizumi.data.manager.SortOrder
import com.dokja.mizumi.data.manager.UserPreferences
import com.dokja.mizumi.data.network.MizuListApi
import com.dokja.mizumi.data.network.UserBookCreateRequest
import com.dokja.mizumi.data.network.models.Book
import com.dokja.mizumi.data.network.models.UserBook
import com.dokja.mizumi.di.AppCoroutineScope
import com.dokja.mizumi.domain.manager.LocalUserManager
import com.dokja.mizumi.isContentUri
import com.dokja.mizumi.presentation.BaseViewModel
import com.dokja.mizumi.presentation.library.toState
import com.dokja.mizumi.presentation.reader.ReaderActivity
import com.dokja.mizumi.repository.AppFileResolver
import com.dokja.mizumi.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.KProperty

interface ChapterStateBundle {
    val rawBookUrl: String
    val bookTitle: String
}

data class search(
    val searchText: String = "",
    val isSearching: Boolean = false,
    val searchResults: List<Book> = emptyList()
)

@HiltViewModel
class BookViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val appScope: AppCoroutineScope,
    private val appFileResolver: AppFileResolver,
    private val localUserManager: LocalUserManager,
    private val api: MizuListApi,
    stateHandle: SavedStateHandle,
) : BaseViewModel(), ChapterStateBundle {
    override val rawBookUrl by StateExtraString(stateHandle)
    override val bookTitle by StateExtraString(stateHandle)

    private val libraryId: String = checkNotNull(stateHandle["libraryId"])

//    val libraryBook = appRepository.libraryBooks.getLibraryFlow(libraryId.toInt())

    val bookUrl = appFileResolver.getLocalIfContentType(rawBookUrl, bookFolderName = bookTitle)

    @Volatile
    private var loadChaptersJob: Job? = null

    @Volatile
    private var lastSelectedChapterUrl: String? = null
    private val book = appRepository.libraryBooks.getLibraryFlow(libraryId.toInt())
        .filterNotNull()
        .map(BookScreenState::BookState)
        .toState(
            viewModelScope,
            BookScreenState.BookState(title = bookTitle, url = bookUrl, coverImageUrl = null)
        )

    private val _userPreferences = MutableStateFlow(
        UserPreferences(
            showUnread = false, // Default value for showUnread
            showBookmarked = false, // Default value for showBookmarked
            sortOrder = SortOrder.Ascending // Default value for sortOrder
        )
    )
    val userPreferences: MutableStateFlow<UserPreferences> = _userPreferences

    val state = BookScreenState(
        book = book,
        error = mutableStateOf(""),
        chapters = mutableStateListOf(),
        selectedChaptersUrl = mutableStateMapOf(),
    )

    private val _trackingDetails = MutableStateFlow<Track?>(null)
    val trackingDetails: StateFlow<Track?> = _trackingDetails.asStateFlow()

    // Update trackingDetails using a dedicated function
    fun updateTrackingDetails(newTrackingDetails: Track?) {
        _trackingDetails.value = newTrackingDetails
    }
    val userIdFlow: Flow<String?> = localUserManager.readUserToken()



    init {
        appScope.launch {
            if (rawBookUrl.isContentUri && appRepository.libraryBooks.get(bookUrl) == null) {
                importUriContent()
            }
        }
        viewModelScope.launch {
            localUserManager.userBookPreferences().collectLatest {
                _userPreferences.value = it
            }
        }

        viewModelScope.launch {
            appRepository.tracker.getTrackByIdWithFlow(libraryId.toInt())
                .flowOn(Dispatchers.IO)
                .collectLatest { _trackingDetails.value = it }
        }


        viewModelScope.launch {
            appRepository.bookChapters.getChaptersWithContextFlow(bookUrl)
                // Sort the chapters given the order preference
                .flowOn(Dispatchers.IO)
                .combine(userPreferences) { chapters, preferences ->
                    val filteredChapters = if (preferences.showUnread) {
                        chapters.filter { !it.chapter.read } // Filter unread chapters
                    } else {
                        chapters // Return all chapters if showUnread is false
                    }
                    val sortedChapters = when (preferences.sortOrder) {
                        SortOrder.Descending -> filteredChapters.sortedByDescending { it.chapter.position }
                        SortOrder.Ascending -> filteredChapters.sortedBy { it.chapter.position }
                    }
                    sortedChapters
                }
                .collect {
                    state.chapters.clear()
                    state.chapters.addAll(it)
                }
        }

        viewModelScope.launch {
            try {
                val userId: String = userIdFlow.firstOrNull() ?: ""
                val response = api.fetchUserBook(userId, trackingDetails.value!!.bookId)
                Log.d("DBResponse", "$response")
                val userBooks = response?.userBooks

                // Extract the UserBook object if it exists
                val userBook: UserBook? = userBooks

                // Calculate lastReadChapter from local chapters
                val lastChapterRead = getLastReadChapterNumber()

                if (userBook != null) {
                    val chaptersRead = lastChapterRead?.let {
                        userBook.chaptersRead?.toInt()?.coerceAtLeast(
                            it
                        )
                    }
                    appRepository.tracker.updateStatus(
                        bookCategory = userBook.bookCategories[1].id,
                        rating = userBook.rating,
                        chaptersRead = chaptersRead.toString(),
                        libraryId = libraryId.toInt(),
                        startedDate = userBook.startedDate,
                        completedAt = userBook.completedAt
                    )
                }
            } catch (e: Exception) {
                Log.e("SearchResult", "Error occurred: ${e.message}", e)
            }
        }
    }

    val searchResults = MutableStateFlow<List<Book>?>(null)
    fun search(englishTitle: String) {
        viewModelScope.launch {
            try {
                val response = api.search(englishTitle)
                if (response.isSuccessful) {
                    val result = response.body()
                    val bookResult = result?.books
                    searchResults.value = bookResult
                    Log.d("SearchResult", result.toString())
                } else {
                    Log.e("SearchResult", "Error occurred: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("SearchResult", "Error occurred: ${e.message}", e)
            }
        }
    }

    suspend fun insertTrack(bookId: String, englishTitle: String?) {
        // Collect the latest value emitted by the flow
        val userId: String = userIdFlow.firstOrNull() ?: ""
        var userBook: UserBook?
        viewModelScope.launch {
            try {
                Log.d("IDSent", userId)
                Log.d("IDSent", bookId)
                val response = api.fetchUserBook(userId, bookId)
                Log.d("DBResponse", "$response")
                val userBooks = response?.userBooks
                // Extract the UserBook object if it exists
                userBook = userBooks

                // Now you can use the userBook object as needed
                Log.d("UserBook", "$userBook")
                if (userBook != null) {
                    Track(
                        libraryId = libraryId.toInt(),
                        bookCategory = userBook!!.bookCategories[1].id,
                        bookId = bookId,
                        title = englishTitle,
                        chaptersRead = getLastReadChapterNumber().toString(),
                        userId = userId,
                        rating = userBook?.rating,
                        startedDate = userBook?.startedDate,
                        completedAt = userBook?.completedAt
                    ).let { appRepository.tracker.insertTrack(it) }

                } else {
                    Log.d("IDSent", intArrayOf(1, 2).toString())
                    val userBookCreateRequest = UserBookCreateRequest(userId, bookId, intArrayOf(1, 2))
                    val userBookCreationRes = api.createUserBook(userBookCreateRequest)
                    Log.d("response", userBookCreationRes.toString())
                }
            }
            catch (e: Exception) {
                Log.e("Error", "Error occurred: ${e.message}", e)
            }
        }
    }

    suspend fun getLastReadChapter(): String? {
        return appRepository.libraryBooks.get(bookUrl)?.lastReadChapter
            ?: appRepository.bookChapters.getFirstChapter(bookUrl)?.url
    }

    private suspend fun getLastReadChapterNumber(): Int? {
        val chapterUrl = getLastReadChapter()
        val chapterTitle = chapterUrl?.let { appRepository.bookChapters.get(it)?.title }
        return if (chapterTitle != null) {
            val splitTitle = chapterTitle.split(" ") // Split on whitespace
            val potentialNumber = splitTitle.firstOrNull { it.matches(Regex("\\d+")) }
            val chapterNumber = potentialNumber?.toIntOrNull() ?: -1
            chapterNumber
        } else null
    }


    fun onOpenLastActiveChapter(context: Context) {
        viewModelScope.launch {
            val lastReadChapter = getLastReadChapter()
                ?: state.chapters.minByOrNull { it.chapter.position }?.chapter?.url
                ?: return@launch
            openBookAtChapter(context, chapterUrl = lastReadChapter)
        }
    }

    private fun openBookAtChapter(context: Context, chapterUrl: String) = goToReader(
        context = context, bookUrl = state.book.value.url, chapterUrl = chapterUrl
    )

    private fun goToReader(context: Context, bookUrl: String, chapterUrl: String) {
        val intent = Intent(context, ReaderActivity::class.java).apply {
            putExtra("bookUrl",  bookUrl)
            putExtra("chapterUrl", chapterUrl)
        }
        context.startActivity(intent)
    }

    fun libraryUpdate() {
        viewModelScope.launch {
            val isBookmarked = appRepository.libraryUpdate(bookTitle = bookTitle, bookUrl = bookUrl)
                val msg = if (isBookmarked) R.string.add_to_library else R.string.remove_from_library
        }
    }

    fun updateShowUnread(showUnread: Boolean) {
        viewModelScope.launch{
            localUserManager.updateUnread(showUnread)
        }
    }

    fun updateSort(sortOrder: SortOrder) {
        viewModelScope.launch{
            localUserManager.updateSort(sortOrder)
        }
    }

    private fun importUriContent() {
        if (loadChaptersJob?.isActive == true) return
        loadChaptersJob = appScope.launch {
            state.error.value = ""
            val rawBookUrl = rawBookUrl
            val bookTitle = bookTitle
            val isInLibrary = appRepository.libraryBooks.existInLibrary(bookUrl)
            appRepository.importEpubFromContentUri(
                contentUri = rawBookUrl,
                bookTitle = bookTitle,
                addToLibrary = isInLibrary
            ).onError { state.error.value = it.message }
        }
    }
}

class StateExtraString(private val state: SavedStateHandle) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) =
        state.get<String>(property.name)!!

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) =
        state.set(property.name, value)
}

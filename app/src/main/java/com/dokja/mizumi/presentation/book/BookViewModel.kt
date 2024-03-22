package com.dokja.mizumi.presentation.book

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dokja.mizumi.R
import com.dokja.mizumi.data.manager.SortOrder
import com.dokja.mizumi.data.manager.UserPreferences
import com.dokja.mizumi.di.AppCoroutineScope
import com.dokja.mizumi.domain.manager.LocalUserManager
import com.dokja.mizumi.isContentUri
import com.dokja.mizumi.presentation.BaseViewModel
import com.dokja.mizumi.presentation.library.toState
import com.dokja.mizumi.repository.AppFileResolver
import com.dokja.mizumi.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.KProperty

interface ChapterStateBundle {
    val rawBookUrl: String
    val bookTitle: String
}

@HiltViewModel
class BookViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val appScope: AppCoroutineScope,
    private val appFileResolver: AppFileResolver,
    private val localUserManager: LocalUserManager,
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
    init {
        appScope.launch {
            if (rawBookUrl.isContentUri && appRepository.libraryBooks.get(bookUrl) == null) {
                importUriContent()
            }
        }

        viewModelScope.launch {
            appRepository.bookChapters.getChaptersWithContextFlow(bookUrl)
                // Sort the chapters given the order preference
                .flowOn(Dispatchers.Default)
                .combine(userPreferences) { chapters, preferences ->
                    val filteredChapters = if (preferences.showUnread) {
                        chapters.filter { !it.chapter.read } // Filter unread chapters
                    } else {
                        chapters // Return all chapters if showUnread is false
                    }
                    filteredChapters
                }
                .collect {
                    state.chapters.clear()
                    state.chapters.addAll(it)
                }
        }

        viewModelScope.launch {
            localUserManager.userBookPreferences().flowOn(Dispatchers.IO).collect {
                _userPreferences.value = it
            }
        }


    }

    private suspend fun getLastReadChapter(): String? {
        return appRepository.libraryBooks.get(bookUrl)?.lastReadChapter
            ?: appRepository.bookChapters.getFirstChapter(bookUrl)?.url
    }

//    suspend fun onOpenLastActiveChapter() {
//        viewModelScope.launch {
//            val lastReadChapter = getLastReadChapter()
//                ?: state.chapters.minByOrNull { it.chapter.position }?.chapter?.url
//                ?: return@launch
//
//            openBookAtChapter(chapterUrl = lastReadChapter)
//        }
//    }
//    fun Context.goToReader(bookUrl: String, chapterUrl: String) {
//        ReaderActivity
//            .IntentData(this, bookUrl = bookUrl, chapterUrl = chapterUrl)
//            .let(::startActivity)
//
//    }
//    fun openBookAtChapter(chapterUrl: String) =
//        goToReader(
//        bookUrl = state.book.value.url, chapterUrl = chapterUrl
//    )





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




//fun removeCommonTextFromTitles(list: List<ChapterWithContext>): List<ChapterWithContext> {
//    // Try removing repetitive title text from chapters
//    if (list.size <= 1) return list
//    val first = list.first().chapter.title
//    val prefix =
//        list.fold(first) { acc, e -> e.chapter.title?.commonPrefixWith(acc, ignoreCase = true) }
//    val suffix =
//        list.fold(first) { acc, e -> e.chapter.title?.commonSuffixWith(acc, ignoreCase = true) }
//
//    // Kotlin Std Lib doesn't have optional ignoreCase parameter for removeSurrounding
//    fun String.removeSurrounding(
//        prefix: CharSequence,
//        suffix: CharSequence,
//        ignoreCase: Boolean = false
//    ): String {
//        if ((length >= prefix.length + suffix.length) && startsWith(prefix, ignoreCase) && endsWith(
//                suffix,
//                ignoreCase
//            )
//        ) {
//            return substring(prefix.length, length - suffix.length)
//        }
//        return this
//    }
//
//    return list.map { data ->
//        val newTitle = data
//            .chapter.title.removeSurrounding(prefix, suffix, ignoreCase = true)
//            .ifBlank { data.chapter.title }
//        data.copy(chapter = data.chapter.copy(title = newTitle))
//    }
//}
package com.dokja.mizumi.presentation.book

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dokja.mizumi.di.AppCoroutineScope
import com.dokja.mizumi.isContentUri
import com.dokja.mizumi.presentation.BaseViewModel
import com.dokja.mizumi.presentation.library.toState
import com.dokja.mizumi.repository.AppFileResolver
import com.dokja.mizumi.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
                .flowOn(Dispatchers.Default)
                .collect { chapters ->
                    val sortedChapters = chapters.sortedByDescending { it.chapter.position }
                    state.chapters.clear()
                    state.chapters.addAll(sortedChapters)
                }
        }
    }
    fun toggleBookmark() {
        viewModelScope.launch {
            val isBookmarked = appRepository.toggleBookmark(bookTitle = bookTitle, bookUrl = bookUrl)
//                val msg = if (isBookmarked) R.string.added_to_library else R.string.removed_from_library
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

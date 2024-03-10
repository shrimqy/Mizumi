package com.dokja.mizumi.presentation.book

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.dokja.mizumi.data.ChapterWithContext
import com.dokja.mizumi.data.local.library.LibraryItem

data class BookScreenState(
    val book: State<BookState>,
    val error: MutableState<String>,
    val selectedChaptersUrl: SnapshotStateMap<String, Unit>,
    val chapters: SnapshotStateList<ChapterWithContext>,
//    val isRefreshing: MutableState<Boolean>,
//    val sourceCatalogNameStrRes: State<Int?>,
//    val settingChapterSort: MutableState<AppPreferences.TERNARY_STATE>,
//    val isLocalSource: State<Boolean>,
//    val isRefreshable: State<Boolean>,
) {

    val isInSelectionMode = derivedStateOf { selectedChaptersUrl.size != 0 }

    data class BookState(
        val title: String,
        val url: String,
        val completed: Boolean = false,
        val lastReadChapter: String? = null,
        val inLibrary: Boolean = false,
        val coverImageUrl: String? = null,
        val description: String? = "",
    ) {
        constructor(book: LibraryItem) : this(
            title = book.title,
            url = book.url,
            completed = book.completed,
            lastReadChapter = book.lastReadChapter,
            inLibrary = book.inLibrary,
            coverImageUrl = book.coverImageUrl,
            description = book.description
        )
    }
}
package com.dokja.mizumi.presentation.book.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.dokja.mizumi.data.ChapterWithContext
import com.dokja.mizumi.presentation.book.BookScreenState

@Composable
fun BookScreenBody(
    state: BookScreenState,
    lazyListState: LazyListState,
    innerPadding: PaddingValues,
    onChapterClick: (chapter: ChapterWithContext) -> Unit,
    onChapterLongClick: (chapter: ChapterWithContext) -> Unit,
) {
    LazyColumn(
        state = lazyListState,
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {

    }
}
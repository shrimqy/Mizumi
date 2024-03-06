package com.dokja.mizumi.presentation.library.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.dokja.mizumi.data.BookWithContext
import com.dokja.mizumi.repository.rememberResolvedBookImagePath

@Composable
fun LibraryComfortableGrid(
    list: List<BookWithContext>,
    contentPadding: PaddingValues,
    onClick: (BookWithContext) -> Unit,
    onLongClick: (BookWithContext) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(105.dp),
        contentPadding = contentPadding
    ) {
        items(
            items = list,
            key = { it.book.url }
        ){
            val interactionSource = remember { MutableInteractionSource() }
            ComfortableGridItem(
                coverData = rememberResolvedBookImagePath(
                    bookUrl = it.book.url,
                    imagePath = it.book.coverImageUrl),
                title = it.book.title,
                onClick = { /*TODO*/ },
                onLongClick = { /*TODO*/ })
        }
    }
}
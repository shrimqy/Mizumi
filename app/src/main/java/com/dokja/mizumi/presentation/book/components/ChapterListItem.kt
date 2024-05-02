package com.dokja.mizumi.presentation.book.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dokja.mizumi.data.ChapterWithContext
import com.dokja.mizumi.presentation.utils.ReadItemAlpha
import com.dokja.mizumi.presentation.utils.SecondaryItemAlpha
import com.dokja.mizumi.presentation.utils.selectedBackground


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChapterListItem(
    chapterWithContext: ChapterWithContext,
    bookmark: Boolean,
    readProgress: String?,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val chapter = chapterWithContext.chapter
    val textAlpha = if (chapter.read) ReadItemAlpha else 1f
    val textSubtitleAlpha = if (chapter.read) ReadItemAlpha else SecondaryItemAlpha

    Row (
        modifier = modifier
            .selectedBackground(selected)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(start = 16.dp, top = 12.dp, end = 8.dp, bottom = 12.dp)
    ){
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                var textHeight by remember { mutableIntStateOf(0) }
                if (bookmark) {
                    Icon(
                        imageVector = Icons.Filled.Bookmark,
                        contentDescription = "Bookmark",
                        modifier = Modifier
                            .sizeIn(maxHeight = with(LocalDensity.current) { textHeight.toDp() - 2.dp }),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                Text(
                    text = chapter.title!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalContentColor.current.copy(alpha = textAlpha),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    onTextLayout = { textHeight = it.size.height },
                )
            }
            Row {
                ProvideTextStyle(
                    value = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        color = LocalContentColor.current.copy(alpha = textSubtitleAlpha),
                    ),
                ) {
                    if (readProgress != null && !chapter.read && readProgress != "0") {
                        Text(
                            text = "Read $readProgress Pages",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = LocalContentColor.current.copy(alpha = textAlpha),
                        )
                    }
                }
            }
        }
    }
}

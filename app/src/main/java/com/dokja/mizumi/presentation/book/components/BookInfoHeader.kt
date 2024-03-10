package com.dokja.mizumi.presentation.book.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.dokja.mizumi.R
import com.dokja.mizumi.presentation.book.BookScreenState
import com.dokja.mizumi.presentation.common.MangaCover
import com.dokja.mizumi.presentation.utils.clickableNoIndication
import com.dokja.mizumi.presentation.utils.copyToClipboard
import com.dokja.mizumi.repository.rememberResolvedBookImagePath

@Composable
fun BookInfoHeader(
    bookState: BookScreenState.BookState,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    val context by rememberUpdatedState(LocalContext.current)
    val coverImageModel = bookState.coverImageUrl?.let {
        rememberResolvedBookImagePath(
            bookUrl = bookState.url,
            imagePath = it
        )
    } ?: R.drawable.cover_error

    Box(modifier = modifier) {
        Box {
            // Backdrop
            val backdropGradientColors = listOf(
                Color.Transparent,
                MaterialTheme.colorScheme.background,
            )
            MangaCover.Book(
                data = coverImageModel,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            brush = Brush.verticalGradient(colors = backdropGradientColors),
                        )
                    }
                    .blur(0.2.dp)
                    .alpha(0.2f),
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = paddingValues.calculateTopPadding())
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MangaCover.Book(
                modifier = Modifier
                    .sizeIn(maxWidth = 120.dp)
                    .align(Alignment.Top),
                data = coverImageModel,
                shape = MaterialTheme.shapes.small
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {

                Text(
                    text = bookState.title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.clickableNoIndication(
                        onLongClick = {
                            if (bookState.title.isNotBlank()) {
                                context.copyToClipboard(
                                    bookState.title,
                                    bookState.title,
                                )
                            }
                        },
                        onClick = {  },
                    ),
                    textAlign = LocalTextStyle.current.textAlign,
                )

                Spacer(modifier = Modifier.height(2.dp))

//                Row(
//                    modifier = Modifier.secondaryItemAlpha(),
//                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.extraSmall),
//                    verticalAlignment = Alignment.CenterVertically,
//                ) {
//                    Icon(
//                        imageVector = Icons.Filled.PersonOutline,
//                        contentDescription = null,
//                        modifier = Modifier.size(16.dp),
//                    )
//                    Text(
//                        text = author?.takeIf { it.isNotBlank() }
//                            ?: stringResource(MR.strings.unknown_author),
//                        style = MaterialTheme.typography.titleSmall,
//                        modifier = Modifier
//                            .clickableNoIndication(
//                                onLongClick = {
//                                    if (!author.isNullOrBlank()) {
//                                        context.copyToClipboard(
//                                            author,
//                                            author,
//                                        )
//                                    }
//                                },
//                                onClick = { if (!author.isNullOrBlank()) doSearch(author, true) },
//                            ),
//                        textAlign = textAlign,
//                    )
//                }
//
//                if (!artist.isNullOrBlank() && author != artist) {
//                    Row(
//                        modifier = Modifier.secondaryItemAlpha(),
//                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.extraSmall),
//                        verticalAlignment = Alignment.CenterVertically,
//                    ) {
//                        Icon(
//                            imageVector = Icons.Filled.Brush,
//                            contentDescription = null,
//                            modifier = Modifier.size(16.dp),
//                        )
//                        Text(
//                            text = artist,
//                            style = MaterialTheme.typography.titleSmall,
//                            modifier = Modifier
//                                .clickableNoIndication(
//                                    onLongClick = { context.copyToClipboard(artist, artist) },
//                                    onClick = { doSearch(artist, true) },
//                                ),
//                            textAlign = textAlign,
//                        )
//                    }
//                }
            }
        }
    }
}


@Composable
fun BookActionRow() {
    
}
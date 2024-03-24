package com.dokja.mizumi.presentation.reader

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dokja.mizumi.presentation.reader.components.bottomBar.BottomReaderBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    state: ReaderScreenState,
    onKeepScreenOn: Boolean,
    onTextFontChanged: (String) -> Unit,
    onTextSizeChanged: (Float) -> Unit,
    onPressBack: () -> Unit,
    readerContent: @Composable (paddingValues: PaddingValues) -> Unit,
) {
    val viewModel: ReaderViewModel = hiltViewModel()
    val chapters = viewModel.chaptersLoader.orderedChapters
    // Capture back action when viewing info
    BackHandler(enabled = state.showReaderInfo.value) {
        state.showReaderInfo.value = false
    }

    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = state.showReaderInfo.value,
                enter = expandVertically(expandFrom = Alignment.Top)
                        + fadeIn(),
                exit = shrinkVertically(shrinkTowards = Alignment.Top)
                        + fadeOut(),
            ) {
                TopAppBar(
                    modifier = Modifier.padding(bottom = 5.dp),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.99f),
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.99f),
                    ),
                    title = {
                        Column(
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = state.readerInfo.bookTitle.value,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.animateContentSize()
                            )
                            Text(
                                text = state.readerInfo.chapterTitle.value,
                                style = MaterialTheme.typography.titleSmall,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.animateContentSize()
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onPressBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                        }
                    },
                    actions = {
                    }
                )
//                Column(
//                    modifier = Modifier
//                        .padding(bottom = 8.dp)
//                        .padding(horizontal = 16.dp),
//                ) {
//                    Text(
//                        text = stringResource(
//                            id = R.string.show_chapter_number,
//                            state.readerInfo.chapterCurrentNumber.value,
//                            state.readerInfo.chaptersCount.value,
//                        ),
//                        style = MaterialTheme.typography.labelMedium,
//                    )
//                    Text(
//                        text = stringResource(
//                            id = R.string.show_chapter_number,
//                            state.readerInfo.chapterPercentageProgress.value
//                        ),
//                        style = MaterialTheme.typography.labelMedium,
//                    )
//                }
            }
        },
        content = readerContent,
        bottomBar = {
            BottomReaderBar(
                state = state,
                chapters = chapters
            )
        }
    )
}
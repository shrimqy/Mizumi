package com.dokja.mizumi.presentation.book

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dokja.mizumi.R
import com.dokja.mizumi.presentation.book.components.BookActionRow
import com.dokja.mizumi.presentation.book.components.BookInfoHeader
import com.dokja.mizumi.presentation.book.components.ChapterHeader
import com.dokja.mizumi.presentation.book.components.ChapterListItem
import com.dokja.mizumi.presentation.book.components.ExpandableMangaDescription
import com.dokja.mizumi.presentation.common.VerticalFastScroller
import com.dokja.mizumi.presentation.common.screens.EmptyScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookScreen(
    rawBookUrl: String,
    bookTitle: String,
    libraryId: String,
    rootNavController: NavController
) {
    val viewModel: BookViewModel = hiltViewModel()
    val state = viewModel.state
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    var showDropDown by rememberSaveable { mutableStateOf(false) }
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()

    //Custom topBarTitleColor
    val topAppBarElementColor = if (scrollBehavior.state.overlappedFraction > 0) {
        MaterialTheme.colorScheme.onBackground
    } else {
        Color.Transparent
    }
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
                TopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    ),
                    title = {
                        Text(
                            text = state.book.value.title,
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = topAppBarElementColor
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { rootNavController.navigateUp() }
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { showBottomSheet = !showBottomSheet }
                        ) {
                            Icon(
                                Icons.Filled.FilterList, "Filter"
                            )
                        }
                        IconButton(onClick = { showDropDown = !showDropDown }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_overflow_24dp),
                                contentDescription = "Overflow"
                            )
                            DropdownMenu(
                                expanded = showDropDown,
                                onDismissRequest = { showDropDown = false }) {

                            }
                        }
                    }
                )
        },
        bottomBar = {
        }
    ) { contentPadding ->
        val topPadding = contentPadding.calculateTopPadding()
        val layoutDirection = LocalLayoutDirection.current
        VerticalFastScroller(
            listState = lazyListState,
            topContentPadding = topPadding,
            endContentPadding = contentPadding.calculateEndPadding(layoutDirection)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = contentPadding.calculateStartPadding(layoutDirection),
                    end = contentPadding.calculateEndPadding(layoutDirection),
                    bottom = contentPadding.calculateBottomPadding()
                )
            ) {
                item(
                    key = "header",
                    contentType = { 0 }
                ) {
                    BookInfoHeader(state.book.value, paddingValues = contentPadding, modifier = Modifier.padding(bottom = 12.dp))
                }

                item(
                    key = "actionRow",
                    contentType = { 1 }
                ) {
                    BookActionRow(
                        inLibrary = state.book.value.inLibrary,
                        trackingStatus = false,
                        onAddToLibraryClicked = { /*TODO*/ },
                        onTrackingClicked = { /*TODO*/ },
                        onEditIntervalClicked = { /*TODO*/ },
                        onEditCategory = { /*TODO*/ })
                }

                item(
                    key = "description",
                    contentType = { 2 }
                ) {
                    ExpandableMangaDescription(
                        defaultExpandState = false,
                        description = state.book.value.description,
                        tagsProvider = { null },
                        onTagSearch = {  },
                        onCopyTagToClipboard = {},
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                }

                item(
                    key = "chapterHeader",
                    contentType = { 3 }
                ) {
                    ChapterHeader(enabled = true, chapterCount = state.chapters.size, onClick = {  })
                }
                items(
                    items = state.chapters.reversed(),
                    key = { "_" + it.chapter.url},
                    contentType = { 4 }
                ) {
                    ChapterListItem(
                        chapterWithContext = it,
                        selected = false,
                        onClick = {},
                        onLongClick = {},
                        bookmark = false,
                        readProgress = null,
                    )
                }
                if (state.error.value.isNotBlank()) item(
                    key = "error",
                    contentType = { 2 }
                ) {
                    EmptyScreen(message = state.error.value)
                }
            }
        }
    }
}
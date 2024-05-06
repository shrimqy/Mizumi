package com.dokja.mizumi.presentation.book.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.dokja.mizumi.R
import com.dokja.mizumi.data.local.tracker.Track
import com.dokja.mizumi.data.network.models.Book
import com.dokja.mizumi.presentation.book.BookViewModel
import com.dokja.mizumi.presentation.common.material.SearchBar
import com.dokja.mizumi.presentation.utils.copyToClipboard
import kotlinx.coroutines.launch

private const val UnsetStatusTextAlpha = 0.5F
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BookTrackerSheet(
    tracker: Track?,
) {
    val viewModel: BookViewModel = hiltViewModel()
    val startDatePickerState = rememberDatePickerState()
    val completedDatePickerState = rememberDatePickerState()
    var showCompletedDate by rememberSaveable { mutableStateOf(false) }
    var showStartedDate by rememberSaveable { mutableStateOf(false) }
    var showScore by rememberSaveable { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .padding(20.dp)
            .animateContentSize()
    ) {
        if (tracker!=null) {
            fun getTrackerStatusString(categoryId: Int): Int {
                return when (categoryId) {
                    1 -> R.string.reading // Reading
                    2 -> R.string.completed // Completed
                    3 -> R.string.dropped // Dropped
                    4 -> R.string.on_hold // On hold
                    5 -> R.string.paused // Paused
                    6 -> R.string.plan_to_read // Plan to read
                    else -> throw IllegalArgumentException("Invalid category ID: $categoryId")
                }
            }

            val context = LocalContext.current
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = { },
                            onLongClick = {
                                context.copyToClipboard(tracker.title!!, tracker.title)
                            },
                        ),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Text(
                        text = tracker.title!!,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp)),
                ) {
                    Column {
                        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                            TrackDetailsItem(
                                modifier = Modifier.weight(1f),
                                text = tracker.bookCategory?.let {
                                    stringResource(
                                        getTrackerStatusString(tracker.bookCategory)
                                    )
                                } ?: "",
                                onClick = { /*  Status Click */ },
                            )
                            VerticalDivider()
                            TrackDetailsItem(
                                modifier = Modifier.weight(1f),
                                text = tracker.chaptersRead,
                                onClick = { /* Chapter Click */ },
                            )
                            if (!showScore) {
                                VerticalDivider()
                                TrackDetailsItem(
                                    modifier = Modifier
                                        .weight(1f)
                                        .alpha(if (tracker.rating == null) UnsetStatusTextAlpha else 1f),
                                    text = tracker.rating ?: stringResource(R.string.score),
                                    onClick = { showScore = true },
                                )
                            }
                        }

                        if (!showCompletedDate && !showStartedDate) {
                            HorizontalDivider()
                            Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                                TrackDetailsItem(
                                    modifier = Modifier.weight(1F),
                                    text = tracker.startedDate,
                                    placeholder = stringResource(R.string.track_started_reading_date),
                                    onClick = { showStartedDate = true },
                                )
                                VerticalDivider()
                                TrackDetailsItem(
                                    modifier = Modifier.weight(1F),
                                    text = tracker.completedAt,
                                    placeholder = stringResource(R.string.track_finished_reading_date),
                                    onClick = { showCompletedDate = true },
                                )
                            }
                        }
                    }
                }
            }
        }
    }

//    if (showSelectedDate) {
//        Dialog(onDismissRequest = { showSelectedDate = false }, properties = DialogProperties(dismissOnClickOutside = true, dismissOnBackPress = true)) {
//            Card(
//                modifier = Modifier,
//                shape = MaterialTheme.shapes.large,
//            ) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxSize(),
//                    verticalArrangement = Arrangement.Center,
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                ) {
//                    DatePicker(title = { Text(text = "Start Date") }, state = startDatePickerState)
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth(),
//                        horizontalArrangement = Arrangement.Center,
//                    ) {
//                        TextButton(
//                            onClick = { showSelectedDate = false },
//                            modifier = Modifier.padding(8.dp),
//                        ) {
//                            Text("Cancel")
//                        }
//                        TextButton(
//                            onClick = {  },
//                            modifier = Modifier.padding(8.dp),
//                        ) {
//                            Text("Ok")
//                        }
//                    }
//                }
//            }
//
//        }
//    }

//    Dialog(onDismissRequest = { showSelectedDate = false }, properties = DialogProperties(dismissOnClickOutside = true, dismissOnBackPress = true)) {
////        DatePicker(title = { Text(text = "Completed Date") }, state = completedDatePickerState)
//    }
}


@Composable
private fun TrackDetailsItem(
    text: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
) {
    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .fillMaxHeight()
            .padding(12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text ?: placeholder,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (text == null) UnsetStatusTextAlpha else 1f),
        )
    }
}


@Composable
fun BookSearchSheet(
    searchQuery: String,
) {
    val lazyListState = rememberLazyListState()
    var text by remember { mutableStateOf(searchQuery) }
    var active by remember { mutableStateOf(false) }
    val viewModel: BookViewModel = hiltViewModel()
    val results = viewModel.searchResults.collectAsState()
    LaunchedEffect(text) {
        viewModel.search(text)
    }
    val scope = rememberCoroutineScope()
    Surface(modifier = Modifier.fillMaxSize()) {
        Column {
            SearchBar(
                query = text,
                onQueryChange = { text = it },
                onSearch = { viewModel.search(text) },
                active = active,
                onActiveChange = { active = it },
                trailingIcon = {
                    if (active && text.isNotEmpty()) {
                        IconButton(onClick = { if (text.isNotEmpty()) text = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear Icon"
                            )
                        }
                    }
                }
            )
            if (results.value != null) {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(
                        items = results.value!!,
                        key = { "_" + it.id },
                        contentType = { 1 }
                    ) {
                        BookList(book = it, onClick = { scope.launch {
                            viewModel.insertTrack(bookId = it.id, it.englishTitle)
                        }  }, modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}


@Composable
fun BookList(
    book: Book,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Display cover image if available
            if (!book.coverUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = book.coverUrl,
                    contentDescription = book.englishTitle ?: "Book Cover",
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Display title
            Text(
                text = book.englishTitle ?: "",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}



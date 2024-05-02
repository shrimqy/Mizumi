package com.dokja.mizumi.presentation.book.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.dokja.mizumi.data.network.models.Book
import com.dokja.mizumi.presentation.book.BookViewModel
import com.dokja.mizumi.presentation.common.material.SearchBar
import kotlinx.coroutines.launch

@Composable
fun BookTrackerSheet(
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
                            viewModel.insertTrack(bookId = it.id)
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
            .padding(8.dp)
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
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
            // Display description
            Text(
                text = book.description ?: "",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
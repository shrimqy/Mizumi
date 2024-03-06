package com.dokja.mizumi.presentation.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dokja.mizumi.data.BookWithContext
import com.dokja.mizumi.presentation.common.screens.EmptyScreen
import com.dokja.mizumi.presentation.library.components.LibraryComfortableGrid


@Composable
fun LibraryScreen() {
    val viewModel: LibraryViewModel = hiltViewModel()
    val state = viewModel.allItems.observeAsState(listOf()).value
    val list: List<BookWithContext> by remember {
        derivedStateOf {
            viewModel.itemList
        }
    }
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
                var text by remember { mutableStateOf("") }
                var active by remember { mutableStateOf(false) }


//                SearchBar(
//                    modifier = Modifier.zIndex(1f),
//                    query = text,
//                    onQueryChange = { text = it },
//                    onSearch = { active = false },
//                    active = active,
//                    onActiveChange = { active = it },
//                    placeholder = {
//                        if (active) {
//                            Text(text = "Search title, tags")
//                        } else {
//                            Text(text = "Search your library")
//                        }
//                    },
//                    leadingIcon = {
//                        if (!active) {
//                            Icon(
//                                imageVector = Icons.Default.Search,
//                                contentDescription = "Search Icon"
//                            )
//                        } else {
//                            IconButton(onClick = { active = false }) {
//                                Icon(
//                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
//                                    contentDescription = "Back icon"
//                                )
//                            }
//                        }
//                    },
//                    trailingIcon = {
//                        if (active && text.isNotEmpty()) {
//                            IconButton(onClick = { if (text.isNotEmpty()) text = "" }) {
//                                Icon(
//                                    imageVector = Icons.Default.Clear,
//                                    contentDescription = "Clear Icon"
//                                )
//                            }
//                        }
//                    }
//                )

                if (state.isEmpty()) {
                        EmptyScreen(message = "Your library is empty")
                } else {
                        LibraryComfortableGrid(
                            list = list,
                            contentPadding = PaddingValues(top = 4.dp, bottom = 50.dp, start = 4.dp, end = 4.dp),
                            onClick = {},
                            onLongClick = {}
                        )

                }

        }
    }
}



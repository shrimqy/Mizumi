package com.dokja.mizumi.presentation.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dokja.mizumi.presentation.common.screens.EmptyScreen
import com.dokja.mizumi.presentation.components.material.SearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(navController: NavController) {
    val viewModel: LibraryViewModel = hiltViewModel()
    val state = viewModel.allItems.observeAsState(listOf()).value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 2.dp)
            .background(MaterialTheme.colorScheme.background),
    ) {
        Box(modifier = Modifier.padding()) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(bottom = 50.dp)
            ) {
                var text by remember { mutableStateOf("") }
                var active by remember { mutableStateOf(false) }
                SearchBar(
                    modifier = Modifier,
                    query = text,
                    onQueryChange = { text = it },
                    onSearch = { active = false },
                    active = active,
                    onActiveChange = { active = it },
                    placeholder = {
                        if (active) {
                            Text(text = "Search title, tags")
                        }
                        else {
                            Text(text = "Search your library")
                        }
                    },
                    leadingIcon = {
                        if (!active) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon"
                            )
                        } else {
                            Icon(
                                modifier = Modifier.clickable { active = false },
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Search Icon"
                            )
                        }
                    },
                    trailingIcon = {
                        if (active and text.isNotEmpty()) {
                            Icon(
                                modifier = Modifier.clickable {
                                    if (text.isNotEmpty()) {
                                        text = ""
                                    }
                                },
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Search Icon"
                            )
                        }
                    },
                )


                if (state.isEmpty()) {

                    EmptyScreen(message = "Your library is empty")
                }
            }
        }
    }
}


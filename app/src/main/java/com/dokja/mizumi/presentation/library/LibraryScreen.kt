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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dokja.mizumi.data.BookWithContext
import com.dokja.mizumi.presentation.common.screens.EmptyScreen
import com.dokja.mizumi.presentation.library.components.LibraryComfortableGrid


@Composable
fun LibraryScreen(
    rootNavController: NavController
) {
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
                if (state.isEmpty()) {
                        EmptyScreen(message = "Your library is empty")
                } else {
                        LibraryComfortableGrid(
                            rootNavController = rootNavController,
                            list = list,
                            contentPadding = PaddingValues(top = 4.dp, bottom = 50.dp, start = 4.dp, end = 4.dp),
                            onClick = { book ->
                                rootNavController.navigate(route = "book/${book .book.libraryid}")
                            },
                            onLongClick = {}
                        )
                }

        }
    }
}



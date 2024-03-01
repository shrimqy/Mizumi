package com.dokja.mizumi.presentation.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.dokja.mizumi.presentation.common.screens.EmptyScreen

@Composable
fun HistoryScreen() {
    val viewModel: HistoryViewModel = hiltViewModel()
    val state = viewModel.allItems.observeAsState(listOf()).value

    if (state.isEmpty()) {
        EmptyScreen(message = "Your library is empty")
    }
}
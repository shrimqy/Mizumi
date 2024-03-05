package com.dokja.mizumi.presentation.history

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.dokja.mizumi.presentation.common.screens.EmptyScreen

@Composable
fun HistoryScreen() {
    val viewModel: HistoryViewModel = hiltViewModel()

    EmptyScreen(message = "Your library is empty")
}
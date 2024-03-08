package com.dokja.mizumi.presentation.book

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun BookScreen(
    rawBookUrl: String,
    bookTitle: String,
    libraryId: String,
    rootNavController: NavController
) {
    val viewModel: BookViewModel = hiltViewModel()
}
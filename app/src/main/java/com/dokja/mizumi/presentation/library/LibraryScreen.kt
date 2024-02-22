package com.dokja.mizumi.presentation.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dokja.mizumi.presentation.common.screens.EmptyScreen

@Composable
fun LibraryScreen(navController: NavController) {
    val viewModel: LibraryViewModel = hiltViewModel()
    val state = viewModel.allItems.observeAsState(listOf()).value

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box (
            modifier = Modifier.padding(it)
        ){
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(bottom = 50.dp)
            ) {
                if (state.isEmpty()) {
                    EmptyScreen(message = "Your library is empty")
                }
            }
        }
    }
}
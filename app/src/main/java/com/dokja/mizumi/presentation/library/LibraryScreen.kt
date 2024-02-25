package com.dokja.mizumi.presentation.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dokja.mizumi.presentation.common.screens.EmptyScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(navController: NavController) {
    val viewModel: LibraryViewModel = hiltViewModel()
    val state = viewModel.allItems.observeAsState(listOf()).value

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background),
    ) {

        Box(modifier = Modifier.padding()) {
            Column(
                modifier = Modifier
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


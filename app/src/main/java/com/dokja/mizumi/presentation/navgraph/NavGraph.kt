package com.dokja.mizumi.presentation.navgraph

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.dokja.mizumi.presentation.library.LibraryScreen
import com.dokja.mizumi.presentation.onboarding.OnBoardingScreen
import com.dokja.mizumi.presentation.onboarding.OnBoardingViewModel

@Composable
fun NavGraph(
    startDestination: String
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination) {
        navigation(
            route = Route.AppStartNavigation.route,
            startDestination = Route.OnBoardingScreen.route
        ){


            /** Loading Screen **/
            composable(
                route = Route.OnBoardingScreen.route
            ) {
                val viewModel: OnBoardingViewModel = hiltViewModel()
                OnBoardingScreen(
                    onEvent = viewModel::onEvent
                )
            }
        }

        navigation(
            route = Route.BookNavigation.route,
            startDestination = Route.HomeScreen.route
        ){
            composable(route = Route.HomeScreen.route) {
                LibraryScreen(navController = navController)
            }
        }
    }
}
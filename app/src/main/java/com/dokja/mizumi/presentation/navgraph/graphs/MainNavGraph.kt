package com.dokja.mizumi.presentation.navgraph.graphs

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dokja.mizumi.presentation.Profile.ProfileScreen
import com.dokja.mizumi.presentation.browse.BrowseScreen
import com.dokja.mizumi.presentation.history.HistoryScreen
import com.dokja.mizumi.presentation.library.LibraryScreen
import com.dokja.mizumi.presentation.navgraph.Graph
import com.dokja.mizumi.presentation.navgraph.MainRouteScreen

@Composable
fun MainNavGraph(
    rootNavController: NavHostController,
    homeNavController: NavHostController,
    innerPadding: PaddingValues
) {
    NavHost(
        navController = homeNavController,
        route = Graph.MainScreenGraph,
        startDestination = MainRouteScreen.Library.route,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(route = MainRouteScreen.Library.route) {
            LibraryScreen(rootNavController)
        }
        composable(route = MainRouteScreen.History.route) {
            HistoryScreen()
        }
        composable(route = MainRouteScreen.Browse.route) {
            BrowseScreen()
        }
        composable(route = MainRouteScreen.Profile.route) {
            ProfileScreen()
        }
    }
}
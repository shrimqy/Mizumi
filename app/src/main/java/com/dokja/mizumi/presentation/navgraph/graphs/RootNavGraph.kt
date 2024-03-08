package com.dokja.mizumi.presentation.navgraph.graphs

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dokja.mizumi.presentation.MainScreen
import com.dokja.mizumi.presentation.navgraph.Graph

@Composable
fun RootNavGraph(startDestination: String) {
    val rootNavController = rememberNavController()
    
    NavHost(
        navController = rootNavController,
        route = Graph.RootGraph,
        startDestination = startDestination
    ) {
        onBoardingNavGraph(rootNavController = rootNavController)
        composable(route = Graph.MainScreenGraph) {
            MainScreen(rootNavController = rootNavController)
        }
        libraryNavGraph(rootNavController = rootNavController)
        browseNavGraph(rootNavController = rootNavController)
    }
}
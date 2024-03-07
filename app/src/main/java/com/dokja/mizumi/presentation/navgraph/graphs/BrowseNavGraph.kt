package com.dokja.mizumi.presentation.navgraph.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dokja.mizumi.presentation.browse.BrowseScreen
import com.dokja.mizumi.presentation.navgraph.BrowseRouteScreen
import com.dokja.mizumi.presentation.navgraph.Graph

fun NavGraphBuilder.browseNavGraph(rootNavController: NavHostController) {
    navigation(
        route = Graph.BrowseScreenGraph,
        startDestination = BrowseRouteScreen.BrowseDetail.route
    ) {
        composable(route = BrowseRouteScreen.BrowseDetail.route, ) {
            BrowseScreen()
        }

    }
}
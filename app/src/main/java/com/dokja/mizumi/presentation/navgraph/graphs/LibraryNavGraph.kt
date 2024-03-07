package com.dokja.mizumi.presentation.navgraph.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dokja.mizumi.presentation.navgraph.Graph
import com.dokja.mizumi.presentation.navgraph.LibraryRouteScreen

fun NavGraphBuilder.libraryNavGraph(rootNavController: NavHostController) {
    navigation(
        route = Graph.LibraryScreenGraph,
        startDestination = LibraryRouteScreen.BookDetail.route
    ) {
        composable(route = LibraryRouteScreen.BookDetail.route, ) {

        }
    }
}
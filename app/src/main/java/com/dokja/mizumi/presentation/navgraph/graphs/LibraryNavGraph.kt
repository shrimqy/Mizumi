package com.dokja.mizumi.presentation.navgraph.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.dokja.mizumi.presentation.book.BookScreen
import com.dokja.mizumi.presentation.navgraph.Graph
import com.dokja.mizumi.presentation.navgraph.LibraryRouteScreen

fun NavGraphBuilder.libraryNavGraph(rootNavController: NavHostController) {
    navigation(
        route = Graph.LibraryScreenGraph,
        startDestination = LibraryRouteScreen.BookDetailScreen.route
    ) {
        composable(
            route = "book/{bookUrl}",
            arguments = listOf(navArgument("bookUrl") { type = NavType.StringType })
        ) { backStackEntry ->
            val bookUrl = backStackEntry.arguments?.getString("bookUrl") ?: ""
            BookScreen(bookUrl = bookUrl)
        }
    }
}
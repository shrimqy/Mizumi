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
            route = "book?bookUrl={rawBookUrl}&bookTitle={bookTitle}&libraryId={libraryId}",
            arguments = listOf(
                navArgument("rawBookUrl") {
                    type = NavType.StringType
                },
                navArgument("bookTitle") {
                    type = NavType.StringType
                },
                navArgument("libraryId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val libraryId = backStackEntry.arguments?.getString("libraryId") ?: ""
            val bookTitle = backStackEntry.arguments?.getString("bookTitle") ?: ""
            val rawBookUrl = backStackEntry.arguments?.getString("rawBookUrl") ?: ""
            BookScreen(libraryId = libraryId, rawBookUrl = rawBookUrl, bookTitle = bookTitle, rootNavController = rootNavController)
        }
    }
}
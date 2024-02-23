package com.dokja.mizumi.presentation.navgraph

import androidx.compose.ui.graphics.vector.ImageVector

sealed class Route(
//    val selectedIcon: ImageVector,
//    val unselectedIcon: ImageVector,
//    val iconTextId: Int,
//    val titleText: Int,
    val route: String
){
    data object OnBoardingScreen: Route(
        route = "onBoardingScreen"
    )
    data object Library: Route(route = "libraryScreen")
    data object Browse: Route(route = "browseScreen")
    data object History: Route(route = "historyScreen")
    data object More: Route(route = "moreScreen")
    data object AppStartNavigation: Route(route = "appStartNavigation")
    data object BookNavigation: Route(route = "bookNavigation")
    data object NavigationScreen:  Route(route = "Navigator")
}

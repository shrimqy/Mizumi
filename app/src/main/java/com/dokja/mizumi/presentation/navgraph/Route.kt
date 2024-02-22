package com.dokja.mizumi.presentation.navgraph

import androidx.compose.ui.graphics.vector.ImageVector

sealed class Route(
//    val selectedIcon: ImageVector,
//    val unselectedIcon: ImageVector,
//    val iconTextId: Int,
//    val titleTextId: Int,
    val route: String
){
    object OnBoardingScreen: Route(route = "onBoardingScreen")
    object HomeScreen: Route(route = "homeScreen")
    object History: Route(route = "history")
    object AppStartNavigation: Route(route = "appStartNavigation")
    object BookNavigation: Route(route = "bookNavigation")
    object BookNavigationScreen:  Route(route = "bookNavigatork")
}

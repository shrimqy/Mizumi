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
    data object HomeScreen: Route(route = "homeScreen")
    data object History: Route(route = "history")
    data object AppStartNavigation: Route(route = "appStartNavigation")
    data object BookNavigation: Route(route = "bookNavigation")
    data object BookNavigationScreen:  Route(route = "bookNavigator")
}

package com.dokja.mizumi.presentation.navgraph

import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.rememberNavController
import com.dokja.mizumi.R
import com.dokja.mizumi.presentation.components.material.NavBar
import com.dokja.mizumi.presentation.components.material.NavigationItem
import com.dokja.mizumi.presentation.library.LibraryScreen

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun MizumiNavigator() {
    val libraryAnimatedIcon = AnimatedImageVector.animatedVectorResource(R.drawable.anim_library_enter)
    val moreAnimatedIcon = AnimatedImageVector.animatedVectorResource(R.drawable.anim_more_enter)
    val browseAnimatedIcon = AnimatedImageVector.animatedVectorResource(R.drawable.anim_browse_enter)
    val historyAnimatedIcon = AnimatedImageVector.animatedVectorResource(R.drawable.anim_history_enter)
    val NavigationItem = remember {
        listOf(
            NavigationItem(libraryAnimatedIcon, text = "Library"),
            NavigationItem(historyAnimatedIcon, text = "History"),
            NavigationItem(browseAnimatedIcon, text = "Browse"),
            NavigationItem(moreAnimatedIcon, text = "More"),
        )
    }

    val navController = rememberNavController()
    val backStackState = navController.currentBackStackEntryAsState().value
    var selectedItem by rememberSaveable {
        mutableStateOf(0)
    }
    selectedItem = when (backStackState?.destination?.route) {
        Route.Library.route -> 0
        Route.History.route -> 1
        Route.Browse.route -> 2
        Route.More.route -> 3
        else -> 0
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(), bottomBar = {
            NavBar(
                items = NavigationItem,
                selectedItem = selectedItem,
                onItemClick = { index ->
                    when (index) {
                        0 -> navigateToTab(
                            navController = navController,
                            route = Route.Library.route
                        )

                        1 -> navigateToTab(
                            navController = navController,
                            route = Route.History.route
                        )

                        2 -> navigateToTab(
                            navController = navController,
                            route = Route.Browse.route
                        )

                        3 -> navigateToTab(
                            navController = navController,
                            route = Route.More.route
                        )
                    }
                })
        }
    ) {
        val bottomPadding = it.calculateBottomPadding()
        NavHost(
            navController = navController,
            startDestination = Route.Library.route,
            modifier = Modifier.padding(bottom = bottomPadding)
        ) {
            composable(route = Route.Library.route) { backStackState ->
                LibraryScreen(navController = navController)
            }
        }
    }
}


private fun navigateToTab(navController: NavController, route: String) {
    navController.navigate(route) {
        navController.graph.startDestinationRoute?.let { screen_route ->
            popUpTo(screen_route) {
                saveState = true
            }
        }
        launchSingleTop = true
        restoreState = true
    }
}
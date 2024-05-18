package com.dokja.mizumi.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dokja.mizumi.R
import com.dokja.mizumi.presentation.common.material.AppTopBar
import com.dokja.mizumi.presentation.common.material.NavBar
import com.dokja.mizumi.presentation.common.material.NavigationItem
import com.dokja.mizumi.presentation.navgraph.MainRouteScreen
import com.dokja.mizumi.presentation.navgraph.graphs.MainNavGraph
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun MainScreen(
    rootNavController: NavHostController,
    homeNavController: NavHostController = rememberNavController()
) {

    val showBottomNavEvent = Channel<Boolean>()

    val libraryAnimatedIcon = AnimatedImageVector.animatedVectorResource(R.drawable.anim_library_enter)
    val moreAnimatedIcon = AnimatedImageVector.animatedVectorResource(R.drawable.anim_more_enter)
    val browseAnimatedIcon = AnimatedImageVector.animatedVectorResource(R.drawable.anim_browse_enter)
    val historyAnimatedIcon = AnimatedImageVector.animatedVectorResource(R.drawable.anim_history_enter)
    val navigationItem = remember {
        listOf(
            NavigationItem(libraryAnimatedIcon, text = "Library"),
            NavigationItem(historyAnimatedIcon, text = "History"),
            NavigationItem(browseAnimatedIcon, text = "Browse"),
            NavigationItem(moreAnimatedIcon, text = "More"),
        )
    }


    val backStackState = homeNavController.currentBackStackEntryAsState().value
    var selectedItem by rememberSaveable {
        mutableIntStateOf(0)
    }


    selectedItem = when (backStackState?.destination?.route) {
        MainRouteScreen.Library.route -> 0
        MainRouteScreen.History.route -> 1
        MainRouteScreen.Browse.route -> 2
        MainRouteScreen.Profile.route -> 3
        else -> 0
    }

    //Hide the bottom navigation when the user is in the details screen
    val isBarVisible = remember(key1 = backStackState) {
        backStackState?.destination?.route == MainRouteScreen.Library.route ||
                backStackState?.destination?.route == MainRouteScreen.Browse.route ||
                backStackState?.destination?.route == MainRouteScreen.History.route ||
                backStackState?.destination?.route == MainRouteScreen.Profile.route
    }

    val isImportVisible = remember(key1 = backStackState) {
        backStackState?.destination?.route == MainRouteScreen.Library.route
    }


    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            if (isBarVisible) {
                AppTopBar(
                    items = navigationItem,
                    selectedItem = selectedItem,
                    isImportVisible = isImportVisible)
            }
        },

        bottomBar = {
            if (isBarVisible) {
                val bottomNavVisible by produceState(initialValue = true) {
                    showBottomNavEvent.receiveAsFlow().collectLatest { value = it }
                }
                AnimatedVisibility(
                    visible = bottomNavVisible,
                    enter = expandVertically(),
                    exit = shrinkVertically(),
                ) {
                    NavBar(
                        items = navigationItem,
                        selectedItem = selectedItem,
                        onItemClick = { index ->
                            when (index) {
                                0 -> navigateToTab(
                                    navController = homeNavController,
                                    route = MainRouteScreen.Library.route
                                )

                                1 -> navigateToTab(
                                    navController = homeNavController,
                                    route = MainRouteScreen.History.route
                                )

                                2 -> navigateToTab(
                                    navController = homeNavController,
                                    route = MainRouteScreen.Browse.route
                                )

                                3 -> navigateToTab(
                                    navController = homeNavController,
                                    route = MainRouteScreen.Profile.route
                                )
                            }
                        }
                    )
                }

            }
        }
    ) {innerPadding ->
        MainNavGraph(
            rootNavController = rootNavController,
            homeNavController,
            innerPadding
        )
    }
}

private fun navigateToTab(navController: NavController, route: String) {
    navController.navigate(route) {
        navController.graph.startDestinationRoute?.let { screenRoute ->
            popUpTo(screenRoute ) {
                saveState = true
            }
        }
        launchSingleTop = true
        restoreState = true
    }
}

//@Composable
//fun OnBackClickStateSaver(navController: NavController) {
//    BackHandler(true) {
//        navigateToTab(
//            navController = navController,
//            route = Route.Library.route
//        )
//    }
//}

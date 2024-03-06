package com.dokja.mizumi.presentation.navgraph

import android.net.Uri
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dokja.mizumi.R
import com.dokja.mizumi.presentation.browse.BrowseScreen
import com.dokja.mizumi.presentation.common.material.AppTopBar
import com.dokja.mizumi.presentation.common.material.NavBar
import com.dokja.mizumi.presentation.common.material.NavigationItem
import com.dokja.mizumi.presentation.history.HistoryScreen
import com.dokja.mizumi.presentation.library.LibraryScreen


private const val NAVIGATION_ANIM_DURATION = 300
private const val FADEIN_ANIM_DURATION = 400
private fun enterTransition() = slideInHorizontally(
    initialOffsetX = { NAVIGATION_ANIM_DURATION }, animationSpec = tween(
        durationMillis = NAVIGATION_ANIM_DURATION, easing = FastOutSlowInEasing
    )
) + fadeIn(animationSpec = tween(NAVIGATION_ANIM_DURATION))

private fun exitTransition() = slideOutHorizontally(
    targetOffsetX = { -NAVIGATION_ANIM_DURATION }, animationSpec = tween(
        durationMillis = NAVIGATION_ANIM_DURATION, easing = FastOutSlowInEasing
    )
) + fadeOut(animationSpec = tween(NAVIGATION_ANIM_DURATION))

private fun popEnterTransition() = slideInHorizontally(
    initialOffsetX = { -NAVIGATION_ANIM_DURATION }, animationSpec = tween(
        durationMillis = NAVIGATION_ANIM_DURATION, easing = FastOutSlowInEasing
    )
) + fadeIn(animationSpec = tween(NAVIGATION_ANIM_DURATION))

private fun popExitTransition() = slideOutHorizontally(
    targetOffsetX = { NAVIGATION_ANIM_DURATION }, animationSpec = tween(
        durationMillis = NAVIGATION_ANIM_DURATION, easing = FastOutSlowInEasing
    )
) + fadeOut(animationSpec = tween(NAVIGATION_ANIM_DURATION))



@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun MizumiNavigator() {
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

    val navController = rememberNavController()
    val backStackState = navController.currentBackStackEntryAsState().value
    var selectedItem by rememberSaveable {
        mutableIntStateOf(0)
    }



    selectedItem = when (backStackState?.destination?.route) {
        Route.Library.route -> 0
        Route.History.route -> 1
        Route.Browse.route -> 2
        Route.More.route -> 3
        else -> 0
    }

    //Hide the bottom navigation when the user is in the details screen
    val isBarVisible = remember(key1 = backStackState) {
        backStackState?.destination?.route == Route.Library.route ||
                backStackState?.destination?.route == Route.Browse.route ||
                backStackState?.destination?.route == Route.History.route ||
                backStackState?.destination?.route == Route.More.route
    }

    val isImportVisible = remember(key1 = backStackState) {
        backStackState?.destination?.route == Route.Library.route
    }


    var selectedDocumentURI by remember {
        mutableStateOf<Uri?>(null)
    }
    val documentPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {uri ->
        if(uri == null) return@rememberLauncherForActivityResult
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
                NavBar(
                    items = navigationItem,
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
                    }
                )
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = Route.Library.route,
            modifier = Modifier.padding(it),
            enterTransition = { fadeIn(animationSpec = tween(FADEIN_ANIM_DURATION)) },
            exitTransition = { fadeOut(animationSpec = tween(FADEIN_ANIM_DURATION)) },
            popEnterTransition = { fadeIn(animationSpec = tween(FADEIN_ANIM_DURATION)) },
            popExitTransition = { fadeOut(animationSpec = tween(FADEIN_ANIM_DURATION)) },
        ) {
            composable(
                route = Route.Library.route,
            ) {
                LibraryScreen()
            }
            composable(
                route = Route.History.route,
            ) {
                OnBackClickStateSaver(navController = navController)
                HistoryScreen()
            }
            composable(
                route = Route.Browse.route,
            ) {
                OnBackClickStateSaver(navController = navController)
                BrowseScreen()
            }
        }
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

@Composable
fun OnBackClickStateSaver(navController: NavController) {
    BackHandler(true) {
        navigateToTab(
            navController = navController,
            route = Route.Library.route
        )
    }
}



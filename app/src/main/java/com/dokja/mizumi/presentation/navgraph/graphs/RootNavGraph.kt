package com.dokja.mizumi.presentation.navgraph.graphs

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dokja.mizumi.presentation.MainScreen
import com.dokja.mizumi.presentation.navgraph.Graph

private const val NAVIGATION_ANIM_DURATION = 600
const val FADEIN_ANIM_DURATION = 400

fun enterTransition() = slideInHorizontally(
    initialOffsetX = { NAVIGATION_ANIM_DURATION }, animationSpec = tween(
        durationMillis = NAVIGATION_ANIM_DURATION, easing = FastOutSlowInEasing
    )
) + fadeIn(animationSpec = tween(NAVIGATION_ANIM_DURATION))

fun exitTransition() = slideOutHorizontally(
    targetOffsetX = { -NAVIGATION_ANIM_DURATION }, animationSpec = tween(
        durationMillis = NAVIGATION_ANIM_DURATION, easing = FastOutSlowInEasing
    )
) + fadeOut(animationSpec = tween(NAVIGATION_ANIM_DURATION))

fun popEnterTransition() = slideInHorizontally(
    initialOffsetX = { -NAVIGATION_ANIM_DURATION }, animationSpec = tween(
        durationMillis = NAVIGATION_ANIM_DURATION, easing = FastOutSlowInEasing
    )
) + fadeIn(animationSpec = tween(NAVIGATION_ANIM_DURATION))

fun popExitTransition() = slideOutHorizontally(
    targetOffsetX = { NAVIGATION_ANIM_DURATION }, animationSpec = tween(
        durationMillis = NAVIGATION_ANIM_DURATION, easing = FastOutSlowInEasing
    )
) + fadeOut(animationSpec = tween(NAVIGATION_ANIM_DURATION))

@Composable
fun RootNavGraph(startDestination: String) {
    val rootNavController = rememberNavController()
    
    NavHost(
        navController = rootNavController,
        route = Graph.RootGraph,
        startDestination = startDestination,
        enterTransition = { enterTransition() },
        exitTransition = { exitTransition() },
        popEnterTransition = { popEnterTransition() },
        popExitTransition = { popExitTransition() }
    ) {
        onBoardingNavGraph(rootNavController = rootNavController)
        composable(route = Graph.MainScreenGraph) {
            MainScreen(rootNavController = rootNavController)
        }
        libraryNavGraph(rootNavController = rootNavController)
        browseNavGraph(rootNavController = rootNavController)
    }
}
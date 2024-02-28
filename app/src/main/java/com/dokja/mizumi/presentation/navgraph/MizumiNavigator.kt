package com.dokja.mizumi.presentation.navgraph

import androidx.activity.compose.BackHandler
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dokja.mizumi.R
import com.dokja.mizumi.presentation.browse.BrowseScreen
import com.dokja.mizumi.presentation.components.material.NavBar
import com.dokja.mizumi.presentation.components.material.NavigationItem
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


data class OverflowItems(
    val text: String
)


@OptIn(ExperimentalAnimationGraphicsApi::class, ExperimentalMaterial3Api::class)
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


    var isOverflowExpanded by remember {
        mutableStateOf(false)
    }

    Scaffold(modifier = Modifier
        .fillMaxSize()
        .padding(
            top = 1.dp
        ),
        topBar = {
                if (isBarVisible) {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            titleContentColor = colorResource(id = R.color.display_small),
                        ),
                        title = { Text(text = navigationItem[selectedItem].text) },
                        actions = {
                            Row {
                                IconButton(onClick = { /*TODO*/ }) {
                                    Icon(imageVector = Icons.Default.FilterList, contentDescription = "Filter Icon")
                                }
                                IconButton(onClick = { isOverflowExpanded = true }) {
                                    Icon(painter = painterResource(id = R.drawable.ic_overflow_24dp), contentDescription = "Overflow")
                                }
                                DropdownMenu(
                                    expanded = isOverflowExpanded,
                                    onDismissRequest = { isOverflowExpanded = false },
                                ) {
                                    DropdownMenuItem(
                                        text = { Text(text = "Turn On Incognito Mode") },
                                        onClick = { /*TODO*/ },
                                        leadingIcon = {
                                            Icon(
                                                painter = painterResource(id = R.drawable.eyeglasses_fill0_wght400_grad0_opsz24),
                                                contentDescription = "Incognito Icon"
                                            )
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text(text = "Settings") },
                                        onClick = { /*TODO*/ },
                                        leadingIcon = {
                                            Icon(imageVector = Icons.Outlined.Settings, contentDescription = "Import Icon")
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text(text = "Stats") },
                                        onClick = { /*TODO*/ },
                                        leadingIcon = {
                                            Icon(imageVector = Icons.Outlined.QueryStats, contentDescription = "Import Icon")
                                        }
                                    )
                                    if (isImportVisible) {
                                        DropdownMenuItem(
                                            text = { Text(text = "Import Book") },
                                            onClick = { /*TODO*/ },
                                            leadingIcon = {
                                                Icon(imageVector = Icons.Outlined.FileDownload, contentDescription = "Import Icon")
                                            }
                                        )
                                    }


                                }
                            }
                        }
                    )
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
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
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
        navController.graph.startDestinationRoute?.let { screen_route ->
            popUpTo(screen_route) {
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



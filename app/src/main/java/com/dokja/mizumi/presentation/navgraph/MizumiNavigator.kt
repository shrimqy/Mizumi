package com.dokja.mizumi.presentation.navgraph

import androidx.activity.compose.BackHandler
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
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
import com.dokja.mizumi.presentation.components.material.SearchBar
import com.dokja.mizumi.presentation.history.HistoryScreen
import com.dokja.mizumi.presentation.library.LibraryScreen

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
        mutableStateOf(0)
    }
    selectedItem = when (backStackState?.destination?.route) {
        Route.Library.route -> 0
        Route.History.route -> 1
        Route.Browse.route -> 2
        Route.More.route -> 3
        else -> 0
    }

    Scaffold(modifier = Modifier.padding(top = 10.dp),
        topBar = {
            Column {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        titleContentColor = colorResource(id = R.color.display_small),
                    ),
                    title = { Text(text = navigationItem[selectedItem].text) },
                )
                var text by remember { mutableStateOf("") }
                var active by remember { mutableStateOf(false) }
                SearchBar(
                    modifier = Modifier,
                    query = text,
                    onQueryChange = { text = it },
                    onSearch = { active = false },
                    active = active,
                    onActiveChange = { active = it },
                    placeholder = {
                        Text(text = "Search Books")
                    },
                    leadingIcon = {
                        if (!active) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon"
                            )
                        } else {
                            Icon(
                                modifier = Modifier.clickable { active = false },
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Search Icon"
                            )
                        }
                    },
                    trailingIcon = {
                        if (active) {
                            Icon(
                                modifier = Modifier.clickable {
                                    if (text.isNotEmpty()) {
                                        text = ""
                                    } else {
                                        active = false
                                    }
                                },
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Search Icon"
                            )
                        }
                    },
                )
            }
            
        },

        bottomBar = {
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
                    })

        }
    ) {
        val bottomPadding = it.calculateBottomPadding()
        NavHost(
            navController = navController,
            startDestination = Route.Library.route,
            modifier = Modifier.padding(bottom = bottomPadding)
        ) {
            composable(
                route = Route.Library.route,

            ) { backStackState ->
                LibraryScreen(navController = navController)
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
            popUpTo(screenRoute) {
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



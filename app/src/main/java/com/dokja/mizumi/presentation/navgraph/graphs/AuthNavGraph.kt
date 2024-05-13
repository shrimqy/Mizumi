package com.dokja.mizumi.presentation.navgraph.graphs

import androidx.compose.animation.fadeOut
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dokja.mizumi.presentation.auth.LoginScreen
import com.dokja.mizumi.presentation.auth.RegisterScreen
import com.dokja.mizumi.presentation.navgraph.AuthScreenGraph
import com.dokja.mizumi.presentation.navgraph.Graph
import com.dokja.mizumi.presentation.more.onboarding.OnBoardingViewModel

fun NavGraphBuilder.authNavGraph(rootNavController: NavHostController) {
    navigation(
        route = Graph.AuthScreenGraph,
        startDestination = AuthScreenGraph.Login.route
    ) {
        composable(route = AuthScreenGraph.Login.route, exitTransition = { fadeOut() }) {
            val viewModel: OnBoardingViewModel = hiltViewModel()
            LoginScreen(rootNavController = rootNavController, onEvent = viewModel::onEvent)
        }
        composable(route = AuthScreenGraph.Register.route, exitTransition = { fadeOut() }) {
            RegisterScreen(rootNavController = rootNavController)
        }
    }
}
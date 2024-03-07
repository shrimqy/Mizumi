package com.dokja.mizumi.presentation.navgraph

const val BOOK_ID_ARG_KEY = "bookId"


object Graph {
    const val RootGraph = "rootGraph"
    const val OnboardingGraph = "onBoardingGraph"
    const val MainScreenGraph = "mainScreenGraph"
    const val LibraryScreenGraph = "libraryScreenGraph"
    const val BrowseScreenGraph = "browseScreenGraph"
    const val HistoryScreenGraph = "historyScreenGraph"
    const val ProfileScreenGraph = "profileScreenGraph"
    const val SettingsGraph = "settingsGraph"
}

sealed class OnboardingRoute(var route: String) {
    data object OnboardingScreen: OnboardingRoute("onBoarding")
    data object Login: OnboardingRoute("login")
    data object Register: OnboardingRoute("register")
}

sealed class MainRouteScreen(var route: String) {
    data object Library: MainRouteScreen("library")
    data object History: MainRouteScreen("history")
    data object Browse: MainRouteScreen("browse")
    data object Profile: MainRouteScreen("profile")
}

sealed class LibraryRouteScreen(var route: String) {
    data object BookDetailScreen: LibraryRouteScreen("book")
}

sealed class SettingsRouteScreen(var route: String) {
    data object SettingsDetail: SettingsRouteScreen("settingsDetail")
}

sealed class BrowseRouteScreen(var route: String) {
    data object BrowseDetail: BrowseRouteScreen("browseDetail")
}


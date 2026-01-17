package com.aura.scanlab.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Privacy : Screen("privacy")
    object Permissions : Screen("permissions")
    object Home : Screen("home")
    object History : Screen("history")
    object Encyclopedia : Screen("encyclopedia")
    object Paywall : Screen("paywall")
    object Settings : Screen("settings")
}

sealed class BottomNavItem(val route: String, val title: String, val iconRes: Int? = null) {
    object Scan : BottomNavItem(Screen.Home.route, "Scan")
    object History : BottomNavItem(Screen.History.route, "History")
    object Encyclopedia : BottomNavItem(Screen.Encyclopedia.route, "Library")
}

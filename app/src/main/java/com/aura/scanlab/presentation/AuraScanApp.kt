package com.aura.scanlab.presentation

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aura.scanlab.data.local.PreferenceManager
import com.aura.scanlab.presentation.navigation.AuraNavHost
import com.aura.scanlab.presentation.navigation.Screen
import com.aura.scanlab.presentation.theme.AuraScanTheme
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun AuraScanApp(cameraExecutor: ExecutorService) {
    val context = LocalContext.current
    val activity = context as? Activity
    val viewModel = remember { AuraViewModel(context) }
    val preferenceManager = remember { PreferenceManager(context) }
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val startDestination = if (preferenceManager.isOnboardingComplete()) {
        Screen.Home.route
    } else {
        Screen.Splash.route
    }

    val mainScreens = listOf(Screen.Home.route, Screen.History.route, Screen.Encyclopedia.route, Screen.Settings.route)
    if (currentDestination?.route in mainScreens) {
        BackHandler {
            activity?.finish()
        }
    }

    AuraScanTheme {
        Scaffold(
            bottomBar = {
                if (currentDestination?.route in mainScreens) {
                // Bottom Navigation
                NavigationBar(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = null) },
                        label = { Text(androidx.compose.ui.res.stringResource(com.aura.scanlab.R.string.nav_scan)) },
                        selected = currentDestination?.route == Screen.Home.route,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF00E676),
                            selectedTextColor = Color(0xFF00E676),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color.Transparent
                        ),
                        onClick = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )

                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                        label = { Text(androidx.compose.ui.res.stringResource(com.aura.scanlab.R.string.nav_history)) },
                        selected = currentDestination?.route == Screen.History.route,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF00E676),
                            selectedTextColor = Color(0xFF00E676),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color.Transparent
                        ),
                        onClick = {
                            navController.navigate(Screen.History.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )

                    NavigationBarItem(
                        icon = { Icon(Icons.Default.MenuBook, contentDescription = null) },
                        label = { Text(androidx.compose.ui.res.stringResource(com.aura.scanlab.R.string.nav_library)) },
                        selected = currentDestination?.route == Screen.Encyclopedia.route,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF00E676),
                            selectedTextColor = Color(0xFF00E676),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color.Transparent
                        ),
                        onClick = {
                            navController.navigate(Screen.Encyclopedia.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                        label = { Text(androidx.compose.ui.res.stringResource(com.aura.scanlab.R.string.nav_settings)) },
                        selected = currentDestination?.route == Screen.Settings.route,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF00E676),
                            selectedTextColor = Color(0xFF00E676),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color.Transparent
                        ),
                        onClick = {
                            navController.navigate(Screen.Settings.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                AuraNavHost(
                    navController = navController,
                    cameraExecutor = cameraExecutor,
                    startDestination = startDestination,
                    onOnboardingComplete = {
                        preferenceManager.setOnboardingComplete(true)
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuraScanAppPreview() {
    val mockExecutor = remember { Executors.newSingleThreadExecutor() }
    AuraScanTheme {
        AuraScanApp(cameraExecutor = mockExecutor)
    }
}

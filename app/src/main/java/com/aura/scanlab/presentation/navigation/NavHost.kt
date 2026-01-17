package com.aura.scanlab.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aura.scanlab.presentation.screens.EncyclopediaScreen
import com.aura.scanlab.presentation.screens.HistoryScreen
import com.aura.scanlab.presentation.screens.HomeScreen
import com.aura.scanlab.presentation.screens.PaywallScreen
import com.aura.scanlab.presentation.screens.PermissionsScreen
import com.aura.scanlab.presentation.screens.PrivacyScreen
import com.aura.scanlab.presentation.screens.SplashScreen
import java.util.concurrent.ExecutorService

@Composable
fun AuraNavHost(
    navController: NavHostController,
    cameraExecutor: ExecutorService,
    startDestination: String = Screen.Splash.route,
    onOnboardingComplete: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(onNext = { 
                navController.navigate(Screen.Privacy.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Privacy.route) {
            PrivacyScreen(onNext = { 
                navController.navigate(Screen.Permissions.route) {
                    popUpTo(Screen.Privacy.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Permissions.route) {
            PermissionsScreen(onNext = { 
                onOnboardingComplete()
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Permissions.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Home.route) {
            HomeScreen(cameraExecutor)
        }
        composable(Screen.History.route) {
            HistoryScreen()
        }
        composable(Screen.Encyclopedia.route) {
            EncyclopediaScreen()
        }
        composable(Screen.Paywall.route) {
            PaywallScreen()
        }
        composable(Screen.Settings.route) {
            com.aura.scanlab.presentation.screens.SettingsScreen()
        }
    }
}

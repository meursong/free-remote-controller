package com.freeremote.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.freeremote.presentation.screens.home.HomeScreen
import com.freeremote.presentation.screens.remote.RemoteControlScreen
import com.freeremote.presentation.screens.settings.SettingsScreen
import com.freeremote.presentation.screens.devices.DevicesScreen
import com.freeremote.presentation.screens.chromecast.ChromecastControlScreen

@Composable
fun RemoteNavHost(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = RemoteScreens.Home.route
    ) {
        composable(RemoteScreens.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(RemoteScreens.RemoteControl.route) {
            RemoteControlScreen(navController = navController)
        }

        composable(RemoteScreens.Devices.route) {
            DevicesScreen(navController = navController)
        }

        composable(RemoteScreens.Settings.route) {
            SettingsScreen(navController = navController)
        }

        composable(RemoteScreens.Chromecast.route) {
            ChromecastControlScreen(navController = navController)
        }
    }
}

sealed class RemoteScreens(val route: String) {
    object Home : RemoteScreens("home")
    object RemoteControl : RemoteScreens("remote_control")
    object Devices : RemoteScreens("devices")
    object Settings : RemoteScreens("settings")
    object Chromecast : RemoteScreens("chromecast")
}
package com.freeremote.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.freeremote.presentation.navigation.RemoteNavHost
import com.freeremote.presentation.theme.FreeRemoteControllerTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity of the FreeRemote application.
 *
 * This is the single activity that hosts all the screens using Jetpack Compose
 * and Navigation Component. It serves as the entry point for the UI layer
 * and is annotated with @AndroidEntryPoint for Hilt dependency injection.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is starting.
     *
     * Sets up the Compose UI with the app theme and navigation host.
     * The entire UI is built using declarative Compose components.
     *
     * @param savedInstanceState Bundle containing the activity's previously saved state
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FreeRemoteControllerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    RemoteNavHost(navController = navController)
                }
            }
        }
    }
}
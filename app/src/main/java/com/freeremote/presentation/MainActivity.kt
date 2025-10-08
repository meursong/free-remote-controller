package com.freeremote.presentation

import android.os.Bundle
import android.util.Log
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
        Log.d("MainActivity", "onCreate started")

        // Initialize Cast context in a separate method - completely optional
        initializeCastSafely()

        Log.d("MainActivity", "Setting up Compose UI...")

        try {
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
            Log.d("MainActivity", "Compose UI setup completed")
        } catch (e: Exception) {
            Log.e("MainActivity", "Failed to setup Compose UI", e)
            finish() // Close the app if UI setup fails
        }
    }

    private fun initializeCastSafely() {
        try {
            Log.d("MainActivity", "Checking if Cast SDK is available...")
            // Try to load the Cast class first
            Class.forName("com.google.android.gms.cast.framework.CastContext")
            Log.d("MainActivity", "Cast SDK found, attempting initialization...")

            // Use reflection to avoid direct dependency
            val castContextClass = Class.forName("com.google.android.gms.cast.framework.CastContext")
            val getSharedInstanceMethod = castContextClass.getMethod("getSharedInstance", android.content.Context::class.java)
            val castContext = getSharedInstanceMethod.invoke(null, this)
            Log.d("MainActivity", "Cast context initialized via reflection: $castContext")
        } catch (e: ClassNotFoundException) {
            Log.w("MainActivity", "Cast SDK not found - Cast features will be disabled")
        } catch (e: NoSuchMethodException) {
            Log.e("MainActivity", "Cast SDK method not found", e)
        } catch (e: Exception) {
            Log.e("MainActivity", "Failed to initialize Cast", e)
        }
    }
}
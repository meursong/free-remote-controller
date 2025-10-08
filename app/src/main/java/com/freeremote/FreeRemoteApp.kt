package com.freeremote

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

/**
 * Main application class for FreeRemote.
 *
 * This class serves as the entry point for the application and is responsible for
 * initializing global application state. It's annotated with @HiltAndroidApp to
 * trigger Hilt's code generation and setup dependency injection for the entire app.
 */
@HiltAndroidApp
class FreeRemoteApp : Application() {
    /**
     * Called when the application is starting.
     *
     * This method initializes any required global application components.
     * Currently, it only calls the super implementation, but can be extended
     * to initialize libraries, analytics, crash reporting, etc.
     */
    override fun onCreate() {
        super.onCreate()
        Log.d("FreeRemoteApp", "Application onCreate called")

        // Set up global exception handler for debugging
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("FreeRemoteApp", "Uncaught exception in thread ${thread.name}", throwable)
            // Let the default handler do its job
            Thread.getDefaultUncaughtExceptionHandler()?.uncaughtException(thread, throwable)
        }

        Log.d("FreeRemoteApp", "Application initialization completed")
    }
}
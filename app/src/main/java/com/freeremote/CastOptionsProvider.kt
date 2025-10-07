package com.freeremote

import android.content.Context
import com.google.android.gms.cast.framework.CastOptions
import com.google.android.gms.cast.framework.OptionsProvider
import com.google.android.gms.cast.framework.SessionProvider
import com.google.android.gms.cast.framework.media.CastMediaOptions
import com.google.android.gms.cast.framework.media.NotificationOptions

/**
 * Google Cast SDK options provider for the FreeRemote application.
 *
 * This class is responsible for configuring the Google Cast SDK with appropriate
 * settings for media casting functionality. It's referenced in the AndroidManifest.xml
 * file to initialize Cast support when the application starts.
 */
class CastOptionsProvider : OptionsProvider {
    /**
     * Provides the Cast options configuration for the application.
     *
     * @param context The application context used for configuration
     * @return CastOptions containing the Cast SDK configuration including receiver app ID,
     *         media options, and session management settings
     */
    override fun getCastOptions(context: Context): CastOptions {
        val notificationOptions = NotificationOptions.Builder()
            .setTargetActivityClassName("com.freeremote.presentation.MainActivity")
            .build()

        val mediaOptions = CastMediaOptions.Builder()
            .setNotificationOptions(notificationOptions)
            .build()

        return CastOptions.Builder()
            .setReceiverApplicationId("CC1AD845") // Default Media Receiver app ID
            .setCastMediaOptions(mediaOptions)
            .setEnableReconnectionService(true)
            .setResumeSavedSession(true)
            .build()
    }

    /**
     * Provides additional session providers for custom Cast protocols.
     *
     * @param context The application context
     * @return null as no additional session providers are needed for standard media casting
     */
    override fun getAdditionalSessionProviders(context: Context): List<SessionProvider>? {
        return null
    }
}
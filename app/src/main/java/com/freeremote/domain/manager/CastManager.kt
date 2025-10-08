package com.freeremote.domain.manager

import android.content.Context
import android.net.Uri
import com.google.android.gms.cast.*
import com.google.android.gms.cast.framework.*
import com.google.android.gms.cast.framework.media.RemoteMediaClient
import com.google.android.gms.common.images.WebImage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager class for Google Cast functionality.
 *
 * This singleton class handles all Google Cast operations including device discovery,
 * connection management, media control, and app launching. It provides a reactive
 * interface using Kotlin Flow for observing Cast state changes.
 *
 * @property context Application context used for Cast SDK initialization
 */
@Singleton
class CastManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var castContext: CastContext? = null
    private var castSession: CastSession? = null
    private var remoteMediaClient: RemoteMediaClient? = null

    private val _deviceState = MutableStateFlow<CastDeviceState>(CastDeviceState.NotConnected)
    val deviceState: StateFlow<CastDeviceState> = _deviceState

    private val _availableDevices = MutableStateFlow<List<CastDevice>>(emptyList())
    val availableDevices: StateFlow<List<CastDevice>> = _availableDevices

    init {
        try {
            castContext = CastContext.getSharedInstance(context)
            setupSessionListener()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Sets up the Cast session listener to track connection state changes.
     *
     * This listener handles Cast session lifecycle events and updates
     * the device state accordingly.
     */
    private fun setupSessionListener() {
        castContext?.sessionManager?.addSessionManagerListener(
            object : SessionManagerListener<CastSession> {
                override fun onSessionStarted(session: CastSession, sessionId: String) {
                    castSession = session
                    remoteMediaClient = session.remoteMediaClient
                    _deviceState.value = CastDeviceState.Connected(
                        deviceName = session.castDevice?.friendlyName ?: "Unknown"
                    )
                }

                override fun onSessionEnded(session: CastSession, error: Int) {
                    castSession = null
                    remoteMediaClient = null
                    _deviceState.value = CastDeviceState.NotConnected
                }

                override fun onSessionStarting(p0: CastSession) {}
                override fun onSessionStartFailed(p0: CastSession, p1: Int) {}
                override fun onSessionEnding(p0: CastSession) {}
                override fun onSessionResumed(p0: CastSession, p1: Boolean) {}
                override fun onSessionResumeFailed(p0: CastSession, p1: Int) {}
                override fun onSessionSuspended(p0: CastSession, p1: Int) {}
                override fun onSessionResuming(p0: CastSession, p1: String) {}
            },
            CastSession::class.java
        )
    }

    /**
     * Device discovery is handled automatically by MediaRouteButton.
     * No manual discovery needed - the Cast SDK manages this when
     * users click the Cast button in the UI.
     */

    /**
     * Disconnects from the currently connected Cast device.
     */
    fun disconnect() {
        castContext?.sessionManager?.endCurrentSession(true)
    }

    // Media Control Functions

    /**
     * Starts or resumes playback on the Cast device.
     */
    fun play() {
        remoteMediaClient?.play()
    }

    /**
     * Pauses playback on the Cast device.
     */
    fun pause() {
        remoteMediaClient?.pause()
    }

    /**
     * Stops playback on the Cast device.
     */
    fun stop() {
        remoteMediaClient?.stop()
    }

    /**
     * Seeks to a specific position in the current media.
     *
     * @param position Target position in milliseconds
     */
    fun seek(position: Long) {
        remoteMediaClient?.seek(position)
    }

    /**
     * Sets the volume level for the Cast stream.
     *
     * @param volume Volume level between 0.0 (mute) and 1.0 (max)
     */
    fun setVolume(volume: Double) {
        remoteMediaClient?.setStreamVolume(volume)
    }

    /**
     * Mutes or unmutes the Cast stream.
     *
     * @param muted True to mute, false to unmute
     */
    fun mute(muted: Boolean) {
        remoteMediaClient?.setStreamMute(muted)
    }

    /**
     * Skips forward in the current media by the specified duration.
     *
     * @param seconds Number of seconds to skip forward (default: 10)
     */
    fun skipForward(seconds: Long = 10) {
        remoteMediaClient?.let { client ->
            val currentPosition = client.approximateStreamPosition
            client.seek(currentPosition + (seconds * 1000))
        }
    }

    /**
     * Skips backward in the current media by the specified duration.
     *
     * @param seconds Number of seconds to skip backward (default: 10)
     */
    fun skipBackward(seconds: Long = 10) {
        remoteMediaClient?.let { client ->
            val currentPosition = client.approximateStreamPosition
            val newPosition = maxOf(0, currentPosition - (seconds * 1000))
            client.seek(newPosition)
        }
    }

    // Launch specific apps

    /**
     * Launches the Netflix app on the Cast device.
     */
    fun launchNetflix() {
        android.util.Log.d("CastManager", "Launching Netflix app")
        launchApp("CA5E8412") // Netflix app ID
    }

    /**
     * Launches the YouTube app on the Cast device.
     */
    fun launchYouTube() {
        android.util.Log.d("CastManager", "Launching YouTube app")
        launchApp("233637DE") // YouTube app ID
    }

    /**
     * Launches the Spotify app on the Cast device.
     */
    fun launchSpotify() {
        launchApp("CC32E753") // Spotify app ID
    }

    /**
     * Launches the Disney+ app on the Cast device.
     */
    fun launchDisneyPlus() {
        launchApp("C3DE6BC2") // Disney+ app ID
    }

    /**
     * Launches the Prime Video app on the Cast device.
     */
    fun launchPrimeVideo() {
        launchApp("ADBEB697") // Prime Video app ID
    }

    /**
     * Internal method to launch an app on the Cast device.
     *
     * @param appId The application ID for the Cast receiver app
     */
    private fun launchApp(appId: String) {
        android.util.Log.d("CastManager", "launchApp called with appId: $appId")

        val sessionManager = castContext?.sessionManager
        val currentSession = sessionManager?.currentCastSession
        android.util.Log.d("CastManager", "Current session: $currentSession")

        if (currentSession != null && currentSession.isConnected) {
            android.util.Log.d("CastManager", "Session active, launching app: $appId")

            try {
                // Stop any current media playback
                currentSession.remoteMediaClient?.stop()

                // Method 1: Try launching as a receiver app
                // This works for Cast receiver apps
                val metadata = ApplicationMetadata()
                metadata.applicationId = appId

                // Method 2: For Android TV apps (Netflix, YouTube), use deep linking
                // These apps need special handling
                when(appId) {
                    "CA5E8412" -> { // Netflix
                        // Netflix uses special receiver
                        loadMediaForApp("http://www.netflix.com", "Netflix", appId)
                    }
                    "233637DE" -> { // YouTube
                        // YouTube uses special receiver
                        loadMediaForApp("http://www.youtube.com", "YouTube", appId)
                    }
                    else -> {
                        // For other apps, try standard launch
                        val namespace = "urn:x-cast:com.google.cast.receiver"
                        val message = """{"type":"LAUNCH","appId":"$appId"}"""
                        currentSession.sendMessage(namespace, message)
                            ?.setResultCallback { result ->
                                if (result.status.isSuccess) {
                                    android.util.Log.d("CastManager", "Launch message sent successfully")
                                } else {
                                    android.util.Log.e("CastManager", "Failed to send launch message: ${result.status}")
                                }
                            }
                    }
                }

            } catch (e: Exception) {
                android.util.Log.e("CastManager", "Exception launching app: ${e.message}")
                e.printStackTrace()
            }
        } else {
            android.util.Log.e("CastManager", "No Cast session active. Please connect first.")
        }
    }

    /**
     * Helper method to load media with app-specific handling
     */
    private fun loadMediaForApp(url: String, appName: String, appId: String) {
        val mediaMetadata = MediaMetadata(MediaMetadata.MEDIA_TYPE_GENERIC).apply {
            putString(MediaMetadata.KEY_TITLE, appName)
        }

        val mediaInfo = MediaInfo.Builder(url)
            .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
            .setContentType("application/dash+xml")
            .setMetadata(mediaMetadata)
            .setCustomData(org.json.JSONObject().apply {
                put("appId", appId)
            })
            .build()

        val loadOptions = MediaLoadOptions.Builder()
            .setAutoplay(true)
            .build()

        remoteMediaClient?.load(mediaInfo, loadOptions)
            ?.setResultCallback { result ->
                if (result.status.isSuccess) {
                    android.util.Log.d("CastManager", "Successfully loaded media for app: $appName")
                } else {
                    android.util.Log.e("CastManager", "Failed to load media: ${result.status}")
                }
            }
    }

    /**
     * Loads media content onto the Cast device with metadata.
     *
     * @param url The URL of the media content to load
     * @param title The title of the media
     * @param subtitle The subtitle or description of the media
     * @param imageUrl Optional URL for the media's thumbnail image
     * @param contentType MIME type of the media (default: "video/mp4")
     */
    fun loadMedia(
        url: String,
        title: String = "",
        subtitle: String = "",
        imageUrl: String? = null,
        contentType: String = "video/mp4"
    ) {
        val movieMetadata = MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE).apply {
            putString(MediaMetadata.KEY_TITLE, title)
            putString(MediaMetadata.KEY_SUBTITLE, subtitle)
            imageUrl?.let {
                addImage(WebImage(Uri.parse(it)))
            }
        }

        val mediaInfo = MediaInfo.Builder(url)
            .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
            .setContentType(contentType)
            .setMetadata(movieMetadata)
            .build()

        val loadOptions = MediaLoadOptions.Builder()
            .setAutoplay(true)
            .setPlayPosition(0)
            .build()

        remoteMediaClient?.load(mediaInfo, loadOptions)
    }

    /**
     * Sends a custom message to the Cast receiver application.
     *
     * @param namespace The namespace for the custom message channel
     * @param message The message content to send
     */
    fun sendMessage(namespace: String, message: String) {
        castSession?.sendMessage(namespace, message)
            ?.setResultCallback { result ->
                if (!result.status.isSuccess) {
                    // Handle error
                }
            }
    }

    /**
     * Represents the connection state of a Cast device.
     */
    sealed class CastDeviceState {
        /** No Cast device is currently connected. */
        data object NotConnected : CastDeviceState()

        /** A Cast device is connected and ready for use.
         * @property deviceName The friendly name of the connected device
         */
        data class Connected(val deviceName: String) : CastDeviceState()

        /** Currently attempting to connect to a Cast device. */
        data object Connecting : CastDeviceState()
    }
}
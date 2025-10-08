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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
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
        val device = currentSession?.castDevice

        android.util.Log.d("CastManager", "Current session: $currentSession")
        android.util.Log.d("CastManager", "Device: ${device?.friendlyName}, Device ID: ${device?.deviceId}")

        if (device != null && sessionManager != null) {
            android.util.Log.d("CastManager", "Device found, attempting to launch app: $appId")

            // Save the device for reconnection
            val savedDevice = device

            // End the current session
            android.util.Log.d("CastManager", "Ending current session...")
            sessionManager.endCurrentSession(true)

            // Launch coroutine to reconnect with a delay
            GlobalScope.launch {
                delay(1000) // Wait for session to fully end

                android.util.Log.d("CastManager", "Session ended, now reconnecting with app: $appId")

                // Get MediaRouter and find the route
                val mediaRouter = androidx.mediarouter.media.MediaRouter.getInstance(context)

                // Find the route that matches our device
                val route = mediaRouter.routes.find {
                    it.name == savedDevice.friendlyName && !it.isDefault
                }

                if (route != null) {
                    android.util.Log.d("CastManager", "Found route: ${route.name}, selecting...")

                    // Store app ID for use when session starts
                    pendingAppId = appId

                    // Register a one-time session started listener
                    sessionManager.addSessionManagerListener(object : SessionManagerListener<CastSession> {
                        override fun onSessionStarted(session: CastSession, sessionId: String) {
                            android.util.Log.d("CastManager", "New session started with pending app: $pendingAppId")

                            // If we have a pending app ID, try to launch it
                            pendingAppId?.let { appId ->
                                android.util.Log.d("CastManager", "Attempting to switch to app: $appId")

                                // Send a message to switch to the app
                                session.sendMessage("urn:x-cast:com.google.cast.system",
                                    """{"type":"LAUNCH","appId":"$appId"}""")
                                    ?.setResultCallback { result ->
                                        if (result.status.isSuccess) {
                                            android.util.Log.d("CastManager", "App launch message sent successfully")
                                        } else {
                                            android.util.Log.e("CastManager", "Failed to send app launch message: ${result.status}")
                                        }
                                    }

                                pendingAppId = null // Clear the pending app
                            }

                            // Remove this listener after use
                            sessionManager.removeSessionManagerListener(this, CastSession::class.java)
                        }

                        override fun onSessionEnded(session: CastSession, error: Int) {}
                        override fun onSessionStarting(session: CastSession) {}
                        override fun onSessionEnding(session: CastSession) {}
                        override fun onSessionResuming(session: CastSession, sessionId: String) {}
                        override fun onSessionResumed(session: CastSession, wasSuspended: Boolean) {}
                        override fun onSessionResumeFailed(session: CastSession, error: Int) {}
                        override fun onSessionSuspended(session: CastSession, reason: Int) {}
                        override fun onSessionStartFailed(session: CastSession, error: Int) {
                            android.util.Log.e("CastManager", "Session start failed: $error")
                            sessionManager.removeSessionManagerListener(this, CastSession::class.java)
                        }
                    }, CastSession::class.java)

                    // Select the route on the main thread
                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                        mediaRouter.selectRoute(route)
                    }
                } else {
                    android.util.Log.e("CastManager", "Could not find route for device: ${savedDevice.friendlyName}")
                }
            }
        } else {
            android.util.Log.e("CastManager", "No Cast device connected")
        }
    }

    // Variable to store the app ID we want to launch
    private var pendingAppId: String? = null

    /**
     * Load Netflix directly
     */
    private fun loadNetflixDirectly() {
        android.util.Log.d("CastManager", "Attempting to load Netflix via Default Media Receiver")

        try {
            // Use Default Media Receiver to show Netflix logo/placeholder
            val mediaMetadata = MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE).apply {
                putString(MediaMetadata.KEY_TITLE, "Netflix")
                putString(MediaMetadata.KEY_SUBTITLE, "Open Netflix on your TV")
                addImage(WebImage(Uri.parse("https://upload.wikimedia.org/wikipedia/commons/0/08/Netflix_2015_logo.svg")))
            }

            val mediaInfo = MediaInfo.Builder("https://www.netflix.com")
                .setStreamType(MediaInfo.STREAM_TYPE_NONE)
                .setContentType("text/html")
                .setMetadata(mediaMetadata)
                .build()

            val loadOptions = MediaLoadOptions.Builder()
                .setAutoplay(false)
                .build()

            remoteMediaClient?.load(mediaInfo, loadOptions)
                ?.setResultCallback { result ->
                    if (result.status.isSuccess) {
                        android.util.Log.d("CastManager", "Netflix placeholder loaded")
                        // Show message to user
                        android.util.Log.i("CastManager", "Please use the Netflix app to cast content")
                    } else {
                        android.util.Log.e("CastManager", "Failed to load Netflix: ${result.status}")
                    }
                }
        } catch (e: Exception) {
            android.util.Log.e("CastManager", "Error loading Netflix: ${e.message}")
        }
    }

    /**
     * Load YouTube directly
     */
    private fun loadYouTubeDirectly() {
        android.util.Log.d("CastManager", "Attempting to load YouTube TV")

        try {
            // Try loading YouTube TV URL which sometimes works on smart TVs
            val mediaMetadata = MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE).apply {
                putString(MediaMetadata.KEY_TITLE, "YouTube")
                putString(MediaMetadata.KEY_SUBTITLE, "Loading YouTube...")
                addImage(WebImage(Uri.parse("https://upload.wikimedia.org/wikipedia/commons/b/b8/YouTube_Logo_2017.svg")))
            }

            // YouTube TV URL that may work on some devices
            val mediaInfo = MediaInfo.Builder("https://www.youtube.com/tv")
                .setStreamType(MediaInfo.STREAM_TYPE_NONE)
                .setContentType("text/html")
                .setMetadata(mediaMetadata)
                .build()

            val loadOptions = MediaLoadOptions.Builder()
                .setAutoplay(false)
                .build()

            remoteMediaClient?.load(mediaInfo, loadOptions)
                ?.setResultCallback { result ->
                    if (result.status.isSuccess) {
                        android.util.Log.d("CastManager", "YouTube TV loaded")
                    } else {
                        android.util.Log.e("CastManager", "Failed to load YouTube: ${result.status}")
                    }
                }
        } catch (e: Exception) {
            android.util.Log.e("CastManager", "Error loading YouTube: ${e.message}")
        }
    }

    /**
     * Load Spotify directly
     */
    private fun loadSpotifyDirectly() {
        android.util.Log.d("CastManager", "Attempting to load Spotify")

        try {
            val mediaMetadata = MediaMetadata(MediaMetadata.MEDIA_TYPE_MUSIC_TRACK).apply {
                putString(MediaMetadata.KEY_TITLE, "Spotify")
                putString(MediaMetadata.KEY_SUBTITLE, "Music Streaming")
                addImage(WebImage(Uri.parse("https://storage.googleapis.com/pr-newsroom-wp/1/2018/11/Spotify_Logo_RGB_Green.png")))
            }

            val mediaInfo = MediaInfo.Builder("https://open.spotify.com")
                .setStreamType(MediaInfo.STREAM_TYPE_NONE)
                .setContentType("text/html")
                .setMetadata(mediaMetadata)
                .build()

            val loadOptions = MediaLoadOptions.Builder()
                .setAutoplay(false)
                .build()

            remoteMediaClient?.load(mediaInfo, loadOptions)
                ?.setResultCallback { result ->
                    if (result.status.isSuccess) {
                        android.util.Log.d("CastManager", "Spotify placeholder loaded")
                    } else {
                        android.util.Log.e("CastManager", "Failed to load Spotify: ${result.status}")
                    }
                }
        } catch (e: Exception) {
            android.util.Log.e("CastManager", "Error loading Spotify: ${e.message}")
        }
    }

    /**
     * Load Disney+ directly
     */
    private fun loadDisneyPlusDirectly() {
        android.util.Log.d("CastManager", "Attempting to load Disney+")

        try {
            val mediaMetadata = MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE).apply {
                putString(MediaMetadata.KEY_TITLE, "Disney+")
                putString(MediaMetadata.KEY_SUBTITLE, "Streaming Service")
            }

            val mediaInfo = MediaInfo.Builder("https://www.disneyplus.com")
                .setStreamType(MediaInfo.STREAM_TYPE_NONE)
                .setContentType("text/html")
                .setMetadata(mediaMetadata)
                .build()

            val loadOptions = MediaLoadOptions.Builder()
                .setAutoplay(false)
                .build()

            remoteMediaClient?.load(mediaInfo, loadOptions)
                ?.setResultCallback { result ->
                    if (result.status.isSuccess) {
                        android.util.Log.d("CastManager", "Disney+ placeholder loaded")
                    } else {
                        android.util.Log.e("CastManager", "Failed to load Disney+: ${result.status}")
                    }
                }
        } catch (e: Exception) {
            android.util.Log.e("CastManager", "Error loading Disney+: ${e.message}")
        }
    }

    /**
     * Load Prime Video directly
     */
    private fun loadPrimeVideoDirectly() {
        android.util.Log.d("CastManager", "Attempting to load Prime Video")

        try {
            val mediaMetadata = MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE).apply {
                putString(MediaMetadata.KEY_TITLE, "Prime Video")
                putString(MediaMetadata.KEY_SUBTITLE, "Amazon Streaming")
            }

            val mediaInfo = MediaInfo.Builder("https://www.primevideo.com")
                .setStreamType(MediaInfo.STREAM_TYPE_NONE)
                .setContentType("text/html")
                .setMetadata(mediaMetadata)
                .build()

            val loadOptions = MediaLoadOptions.Builder()
                .setAutoplay(false)
                .build()

            remoteMediaClient?.load(mediaInfo, loadOptions)
                ?.setResultCallback { result ->
                    if (result.status.isSuccess) {
                        android.util.Log.d("CastManager", "Prime Video placeholder loaded")
                    } else {
                        android.util.Log.e("CastManager", "Failed to load Prime Video: ${result.status}")
                    }
                }
        } catch (e: Exception) {
            android.util.Log.e("CastManager", "Error loading Prime Video: ${e.message}")
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
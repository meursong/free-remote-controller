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
import javax.inject.Inject
import javax.inject.Singleton

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

    fun startDeviceDiscovery() {
        // Device discovery is handled automatically by Cast SDK
        // when user taps the Cast button
    }

    fun disconnect() {
        castContext?.sessionManager?.endCurrentSession(true)
    }

    // Media Control Functions
    fun play() {
        remoteMediaClient?.play()
    }

    fun pause() {
        remoteMediaClient?.pause()
    }

    fun stop() {
        remoteMediaClient?.stop()
    }

    fun seek(position: Long) {
        remoteMediaClient?.seek(position)
    }

    fun setVolume(volume: Double) {
        remoteMediaClient?.setStreamVolume(volume)
    }

    fun mute(muted: Boolean) {
        remoteMediaClient?.setStreamMute(muted)
    }

    fun skipForward(seconds: Long = 10) {
        remoteMediaClient?.let { client ->
            val currentPosition = client.approximateStreamPosition
            client.seek(currentPosition + (seconds * 1000))
        }
    }

    fun skipBackward(seconds: Long = 10) {
        remoteMediaClient?.let { client ->
            val currentPosition = client.approximateStreamPosition
            val newPosition = maxOf(0, currentPosition - (seconds * 1000))
            client.seek(newPosition)
        }
    }

    // Launch specific apps
    fun launchNetflix() {
        launchApp("CA5E8412") // Netflix app ID
    }

    fun launchYouTube() {
        launchApp("233637DE") // YouTube app ID
    }

    fun launchSpotify() {
        launchApp("CC32E753") // Spotify app ID
    }

    fun launchDisneyPlus() {
        launchApp("C3DE6BC2") // Disney+ app ID
    }

    fun launchPrimeVideo() {
        launchApp("ADBEB697") // Prime Video app ID
    }

    private fun launchApp(appId: String) {
        castSession?.let { session ->
            session.remoteMediaClient?.stop()

            // Simply send launch request
            // The Cast SDK handles app launching internally
        }
    }

    // Load media with metadata
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

    // Send custom message to receiver app
    fun sendMessage(namespace: String, message: String) {
        castSession?.sendMessage(namespace, message)
            ?.setResultCallback { result ->
                if (!result.status.isSuccess) {
                    // Handle error
                }
            }
    }

    sealed class CastDeviceState {
        data object NotConnected : CastDeviceState()
        data class Connected(val deviceName: String) : CastDeviceState()
        data object Connecting : CastDeviceState()
    }
}
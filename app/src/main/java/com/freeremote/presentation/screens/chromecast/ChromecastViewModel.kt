package com.freeremote.presentation.screens.chromecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freeremote.domain.manager.CastManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChromecastViewModel @Inject constructor(
    private val castManager: CastManager
) : ViewModel() {

    private val _castState = MutableStateFlow<CastState>(CastState.NotConnected)
    val castState: StateFlow<CastState> = _castState

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration

    private val _volume = MutableStateFlow(0.5f)
    val volume: StateFlow<Float> = _volume

    private var isMuted = false

    init {
        observeCastState()
    }

    private fun observeCastState() {
        viewModelScope.launch {
            castManager.deviceState.collect { state ->
                _castState.value = when (state) {
                    is CastManager.CastDeviceState.Connected -> {
                        CastState.Connected(state.deviceName)
                    }
                    is CastManager.CastDeviceState.Connecting -> {
                        CastState.Connecting
                    }
                    is CastManager.CastDeviceState.NotConnected -> {
                        CastState.NotConnected
                    }
                }
            }
        }
    }

    // Device discovery is handled automatically by the MediaRouteButton
    // No need for manual scanning

    fun disconnect() {
        castManager.disconnect()
        _isPlaying.value = false
        _currentPosition.value = 0L
        _duration.value = 0L
    }

    // Media Control
    fun togglePlayPause() {
        if (_isPlaying.value) {
            castManager.pause()
            _isPlaying.value = false
        } else {
            castManager.play()
            _isPlaying.value = true
        }
    }

    fun stop() {
        castManager.stop()
        _isPlaying.value = false
        _currentPosition.value = 0L
    }

    fun seek(position: Long) {
        castManager.seek(position)
        _currentPosition.value = position
    }

    fun skipForward(seconds: Long = 10) {
        castManager.skipForward(seconds)
    }

    fun skipBackward(seconds: Long = 10) {
        castManager.skipBackward(seconds)
    }

    fun setVolume(volume: Float) {
        _volume.value = volume
        castManager.setVolume(volume.toDouble())
        if (volume > 0 && isMuted) {
            isMuted = false
        }
    }

    fun toggleMute() {
        isMuted = !isMuted
        castManager.mute(isMuted)
        if (isMuted) {
            _volume.value = 0f
        } else {
            _volume.value = 0.5f
        }
    }

    // App Launching
    fun launchNetflix() {
        castManager.launchNetflix()
    }

    fun launchYouTube() {
        castManager.launchYouTube()
    }

    fun launchDisneyPlus() {
        castManager.launchDisneyPlus()
    }

    fun launchPrimeVideo() {
        castManager.launchPrimeVideo()
    }

    fun launchSpotify() {
        castManager.launchSpotify()
    }

    // Quick Actions
    fun goHome() {
        // Send home command
        castManager.sendMessage("urn:x-cast:com.google.cast.media", "{\"type\":\"HOME\"}")
    }

    fun toggleSubtitles() {
        // Send subtitle toggle command
        castManager.sendMessage("urn:x-cast:com.google.cast.media", "{\"type\":\"TOGGLE_SUBTITLES\"}")
    }

    fun openSettings() {
        // Send settings command
        castManager.sendMessage("urn:x-cast:com.google.cast.media", "{\"type\":\"SETTINGS\"}")
    }

    // Load custom media
    fun loadMedia(url: String, title: String, subtitle: String = "", imageUrl: String? = null) {
        castManager.loadMedia(
            url = url,
            title = title,
            subtitle = subtitle,
            imageUrl = imageUrl
        )
        _isPlaying.value = true
    }

    sealed class CastState {
        object NotConnected : CastState()
        object Connecting : CastState()
        data class Connected(val deviceName: String) : CastState()
    }
}
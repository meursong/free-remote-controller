package com.freeremote.presentation.screens.chromecast

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freeremote.domain.manager.CastManager
import com.freeremote.domain.manager.DialManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChromecastViewModel @Inject constructor(
    private val castManager: CastManager,
    private val dialManager: DialManager
) : ViewModel() {

    private val _castState = MutableStateFlow<CastState>(CastState.NotConnected)
    val castState: StateFlow<CastState> = _castState

    private val _dialDevices = MutableStateFlow<List<DialManager.DialDevice>>(emptyList())
    val dialDevices: StateFlow<List<DialManager.DialDevice>> = _dialDevices

    private val _dialState = MutableStateFlow<DialManager.ConnectionState>(DialManager.ConnectionState.NotConnected)
    val dialState: StateFlow<DialManager.ConnectionState> = _dialState

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration

    private val _volume = MutableStateFlow(0.5f)
    val volume: StateFlow<Float> = _volume

    private var isMuted = false
    private var useDialForApps = false // Flag to determine whether to use DIAL protocol

    init {
        observeCastState()
        observeDialState()
        // Automatically discover DIAL devices
        discoverDialDevices()
    }

    private fun observeCastState() {
        viewModelScope.launch {
            castManager.deviceState.collect { state ->
                _castState.value = when (state) {
                    is CastManager.CastDeviceState.Connected -> {
                        useDialForApps = false // Use Cast when connected
                        CastState.Connected(state.deviceName)
                    }
                    is CastManager.CastDeviceState.Connecting -> {
                        CastState.Connecting
                    }
                    is CastManager.CastDeviceState.NotConnected -> {
                        useDialForApps = true // Fall back to DIAL when Cast is not connected
                        CastState.NotConnected
                    }
                }
            }
        }
    }

    private fun observeDialState() {
        viewModelScope.launch {
            dialManager.connectionState.collect { state ->
                _dialState.value = state
                Log.d("ChromecastViewModel", "DIAL state changed: $state")
            }
        }

        viewModelScope.launch {
            dialManager.availableDevices.collect { devices ->
                _dialDevices.value = devices
                Log.d("ChromecastViewModel", "DIAL devices updated: ${devices.size} device(s)")

                // Auto-connect to first available DIAL device if not connected
                if (devices.isNotEmpty() && _dialState.value is DialManager.ConnectionState.DevicesFound) {
                    val firstDevice = devices.first()
                    Log.d("ChromecastViewModel", "Auto-connecting to DIAL device: ${firstDevice.name}")
                    dialManager.connectToDevice(firstDevice)
                }
            }
        }
    }

    fun discoverDialDevices() {
        viewModelScope.launch {
            Log.d("ChromecastViewModel", "Starting DIAL device discovery...")
            dialManager.discoverDevices()
        }
    }

    fun connectToDialDevice(device: DialManager.DialDevice) {
        dialManager.connectToDevice(device)
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
        // Always try DIAL first for Netflix
        Log.d("ChromecastViewModel", "Launch Netflix - Forcing DIAL protocol")
        dialManager.launchNetflix()
    }

    fun launchYouTube() {
        // Always try DIAL first for YouTube
        Log.d("ChromecastViewModel", "Launch YouTube - Forcing DIAL protocol")
        dialManager.launchYouTube()
    }

    fun launchDisneyPlus() {
        Log.d("ChromecastViewModel", "Launch Disney+ - Using DIAL: $useDialForApps")
        if (useDialForApps || _castState.value !is CastState.Connected) {
            // Use DIAL protocol
            dialManager.launchDisneyPlus()
        } else {
            // Use Cast SDK
            castManager.launchDisneyPlus()
        }
    }

    fun launchPrimeVideo() {
        Log.d("ChromecastViewModel", "Launch Prime Video - Using DIAL: $useDialForApps")
        if (useDialForApps || _castState.value !is CastState.Connected) {
            // Use DIAL protocol
            dialManager.launchPrimeVideo()
        } else {
            // Use Cast SDK
            castManager.launchPrimeVideo()
        }
    }

    fun launchSpotify() {
        Log.d("ChromecastViewModel", "Launch Spotify - Using DIAL: $useDialForApps")
        if (useDialForApps || _castState.value !is CastState.Connected) {
            // Use DIAL protocol
            dialManager.launchSpotify()
        } else {
            // Use Cast SDK
            castManager.launchSpotify()
        }
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
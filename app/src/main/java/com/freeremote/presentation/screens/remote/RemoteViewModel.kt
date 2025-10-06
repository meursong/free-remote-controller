package com.freeremote.presentation.screens.remote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RemoteViewModel @Inject constructor(
    // TODO: Inject IR manager and network manager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RemoteUiState())
    val uiState: StateFlow<RemoteUiState> = _uiState

    fun sendCommand(command: String) {
        viewModelScope.launch {
            // TODO: Send command via IR or network
            _uiState.value = _uiState.value.copy(lastCommand = command)
        }
    }

    fun connectToDevice(deviceId: String) {
        viewModelScope.launch {
            // TODO: Connect to device
        }
    }
}

data class RemoteUiState(
    val isConnected: Boolean = false,
    val lastCommand: String = "",
    val deviceName: String = ""
)
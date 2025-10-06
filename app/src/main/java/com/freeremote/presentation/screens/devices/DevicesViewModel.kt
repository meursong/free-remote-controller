package com.freeremote.presentation.screens.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DevicesViewModel @Inject constructor(
    // TODO: Inject device repository
) : ViewModel() {

    private val _devices = MutableStateFlow<List<Device>>(emptyList())
    val devices: StateFlow<List<Device>> = _devices

    fun addDevice(deviceType: String) {
        viewModelScope.launch {
            // TODO: Add device to database
        }
    }

    fun deleteDevice(deviceId: String) {
        viewModelScope.launch {
            // TODO: Delete device from database
        }
    }

    fun updateDevice(device: Device) {
        viewModelScope.launch {
            // TODO: Update device in database
        }
    }
}

data class Device(
    val id: String,
    val name: String,
    val type: String,
    val brand: String? = null,
    val model: String? = null,
    val irCodes: Map<String, String> = emptyMap()
)
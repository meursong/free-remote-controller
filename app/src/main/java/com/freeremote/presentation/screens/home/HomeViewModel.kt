package com.freeremote.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    // TODO: Inject repositories here
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadRecentDevices()
    }

    private fun loadRecentDevices() {
        viewModelScope.launch {
            // TODO: Load recent devices from database
        }
    }
}

data class HomeUiState(
    val recentDevices: List<DeviceItem> = emptyList(),
    val isLoading: Boolean = false
)
package com.freeremote.presentation.screens.remote

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freeremote.domain.manager.IRManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RemoteViewModel @Inject constructor(
    private val irManager: IRManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RemoteUiState())
    val uiState: StateFlow<RemoteUiState> = _uiState

    // 현재 선택된 TV 브랜드 (기본값: Samsung)
    private var currentTvBrand = TvBrand.SAMSUNG

    init {
        // IR Emitter 지원 여부 확인
        val hasIr = irManager.hasIrEmitter()
        _uiState.value = _uiState.value.copy(
            hasIrEmitter = hasIr,
            deviceName = "거실 TV",
            isConnected = hasIr
        )

        if (!hasIr) {
            Log.w("RemoteViewModel", "이 기기는 IR 블라스터를 지원하지 않습니다.")
        }
    }

    fun sendCommand(command: String) {
        viewModelScope.launch {
            Log.d("RemoteViewModel", "명령 전송: $command")

            // IR 지원 여부 확인
            if (!_uiState.value.hasIrEmitter) {
                Log.e("RemoteViewModel", "IR 블라스터가 없어서 명령을 전송할 수 없습니다.")
                _uiState.value = _uiState.value.copy(
                    lastCommand = command,
                    lastCommandStatus = "IR 블라스터 미지원"
                )
                return@launch
            }

            // TV 브랜드에 따른 IR 코드 가져오기
            val irCodes = when (currentTvBrand) {
                TvBrand.SAMSUNG -> IRManager.IRCodes.SAMSUNG_TV
                TvBrand.LG -> IRManager.IRCodes.LG_TV
                else -> IRManager.IRCodes.SAMSUNG_TV // 기본값
            }

            // 명령에 해당하는 IR 코드 찾기
            val irCode = irCodes[command]

            if (irCode != null) {
                // IR 신호 전송
                val success = irManager.transmit(irCode.frequency, irCode.pattern)

                _uiState.value = _uiState.value.copy(
                    lastCommand = command,
                    lastCommandStatus = if (success) "전송 완료" else "전송 실패"
                )

                Log.d("RemoteViewModel", "IR 신호 전송 ${if (success) "성공" else "실패"}: $command")
            } else {
                // 지원하지 않는 명령
                Log.w("RemoteViewModel", "지원하지 않는 명령: $command")
                _uiState.value = _uiState.value.copy(
                    lastCommand = command,
                    lastCommandStatus = "미지원 명령"
                )
            }
        }
    }

    fun changeTvBrand(brand: TvBrand) {
        currentTvBrand = brand
        _uiState.value = _uiState.value.copy(
            deviceName = "${brand.displayName} TV"
        )
        Log.d("RemoteViewModel", "TV 브랜드 변경: ${brand.displayName}")
    }

    fun connectToDevice(deviceId: String) {
        viewModelScope.launch {
            // WiFi 기반 연결이 필요한 경우 구현
            Log.d("RemoteViewModel", "기기 연결 시도: $deviceId")
        }
    }

    enum class TvBrand(val displayName: String) {
        SAMSUNG("삼성"),
        LG("LG"),
        SONY("소니"),
        PANASONIC("파나소닉")
    }
}

data class RemoteUiState(
    val isConnected: Boolean = false,
    val hasIrEmitter: Boolean = false,
    val lastCommand: String = "",
    val lastCommandStatus: String = "",
    val deviceName: String = ""
)
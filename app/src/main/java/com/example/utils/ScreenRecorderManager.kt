package com.example.utils

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class RecordingConfig(
    val resolution: String = "1080p",
    val bitrateMbps: Int = 16,
    val frameRate: Int = 60,
    val includeAudio: Boolean = true,
    val showTouchPointer: Boolean = true
)

class ScreenRecorderManager(private val context: Context) {

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _recordingDurationSeconds = MutableStateFlow(0L)
    val recordingDurationSeconds: StateFlow<Long> = _recordingDurationSeconds.asStateFlow()

    private val _config = MutableStateFlow(RecordingConfig())
    val config: StateFlow<RecordingConfig> = _config.asStateFlow()

    fun updateConfig(newConfig: RecordingConfig) {
        _config.value = newConfig
    }

    fun startRecording() {
        _isRecording.value = true
        _recordingDurationSeconds.value = 0L
    }

    fun stopRecording() {
        _isRecording.value = false
    }

    fun tickTimer() {
        if (_isRecording.value) {
            _recordingDurationSeconds.value += 1
        }
    }
}

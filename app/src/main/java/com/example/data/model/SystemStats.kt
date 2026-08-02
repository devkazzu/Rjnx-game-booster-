package com.example.data.model

data class SystemStats(
    val cpuUsagePercent: Int = 24,
    val ramUsagePercent: Int = 58,
    val ramUsedMb: Long = 4200,
    val ramTotalMb: Long = 8000,
    val storageUsagePercent: Int = 62,
    val storageUsedGb: Double = 79.4,
    val storageTotalGb: Double = 128.0,
    val batteryPercent: Int = 85,
    val batteryTempC: Float = 34.5f,
    val isCharging: Boolean = false,
    val downloadSpeedKbps: Float = 1240.5f,
    val uploadSpeedKbps: Float = 480.2f,
    val pingMs: Int = 28,
    val packetLossPercent: Float = 0.2f,
    val currentFps: Int = 60,
    val averageFps: Int = 59,
    val minFps: Int = 52,
    val maxFps: Int = 60,
    val frameTimeMs: Float = 16.6f,
    val activeGamePackage: String? = null,
    val activeGameTitle: String? = null,
    val activeMode: PerformanceMode = PerformanceMode.BALANCED
)

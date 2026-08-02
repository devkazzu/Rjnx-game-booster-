package com.example.data.model

data class BoostResult(
    val memoryFreedMb: Long,
    val cpuOptimizedPercent: Int,
    val appsClosedCount: Int,
    val durationMs: Long,
    val timestamp: Long = System.currentTimeMillis()
)

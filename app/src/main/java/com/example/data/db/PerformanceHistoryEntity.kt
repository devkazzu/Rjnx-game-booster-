package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "boost_history")
data class PerformanceHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val modeName: String,
    val memoryFreedMb: Long,
    val cpuOptimizedPercent: Int,
    val appsClosedCount: Int,
    val durationMs: Long
)

package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_stats")
data class GameStatsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageName: String,
    val timestamp: Long,
    val sessionDurationSeconds: Long,
    val avgFps: Int,
    val maxFps: Int,
    val minFps: Int,
    val avgBatteryTempC: Float,
    val peakCpuPercent: Int
)

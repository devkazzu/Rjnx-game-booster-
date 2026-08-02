package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey val packageName: String,
    val title: String,
    val category: String = "Action",
    val iconDrawableResName: String? = null,
    val isFavorite: Boolean = false,
    val isHidden: Boolean = false,
    val lastPlayedTimestamp: Long = 0L,
    val totalTimePlayedSeconds: Long = 0L,
    val performanceModeName: String = "BALANCED",
    val autoDndEnabled: Boolean = true,
    val autoRotationLock: Boolean = true,
    val touchOptimization: Boolean = true,
    val overlayEnabled: Boolean = true,
    val networkPriority: Boolean = true,
    val customBrightnessPercent: Int = 90,
    val customVolumePercent: Int = 80
)

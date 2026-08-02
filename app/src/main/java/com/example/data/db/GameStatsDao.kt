package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameStatsDao {
    @Query("SELECT * FROM game_stats WHERE packageName = :packageName ORDER BY timestamp DESC")
    fun getStatsForGame(packageName: String): Flow<List<GameStatsEntity>>

    @Insert
    suspend fun insertGameStats(stats: GameStatsEntity)
}

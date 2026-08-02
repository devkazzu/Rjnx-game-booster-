package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PerformanceDao {
    @Query("SELECT * FROM boost_history ORDER BY timestamp DESC LIMIT 50")
    fun getBoostHistory(): Flow<List<PerformanceHistoryEntity>>

    @Insert
    suspend fun insertBoostRecord(record: PerformanceHistoryEntity)

    @Query("DELETE FROM boost_history")
    suspend fun clearHistory()
}

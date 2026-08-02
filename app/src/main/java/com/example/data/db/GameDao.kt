package com.example.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM games WHERE isHidden = 0 ORDER BY isFavorite DESC, lastPlayedTimestamp DESC")
    fun getAllVisibleGames(): Flow<List<GameEntity>>

    @Query("SELECT * FROM games WHERE isFavorite = 1 AND isHidden = 0 ORDER BY lastPlayedTimestamp DESC")
    fun getFavoriteGames(): Flow<List<GameEntity>>

    @Query("SELECT * FROM games WHERE isHidden = 1 ORDER BY title ASC")
    fun getHiddenGames(): Flow<List<GameEntity>>

    @Query("SELECT * FROM games WHERE packageName = :packageName")
    suspend fun getGameByPackage(packageName: String): GameEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(game: GameEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(games: List<GameEntity>)

    @Update
    suspend fun update(game: GameEntity)

    @Delete
    suspend fun delete(game: GameEntity)

    @Query("UPDATE games SET lastPlayedTimestamp = :timestamp, totalTimePlayedSeconds = totalTimePlayedSeconds + :addedTimeSeconds WHERE packageName = :packageName")
    suspend fun updatePlayTime(packageName: String, timestamp: Long, addedTimeSeconds: Long)

    @Query("UPDATE games SET isFavorite = :isFavorite WHERE packageName = :packageName")
    suspend fun setFavorite(packageName: String, isFavorite: Boolean)

    @Query("UPDATE games SET isHidden = :isHidden WHERE packageName = :packageName")
    suspend fun setHidden(packageName: String, isHidden: Boolean)
}

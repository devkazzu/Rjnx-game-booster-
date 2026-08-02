package com.example.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import com.example.data.db.AppDatabase
import com.example.data.db.GameEntity
import com.example.data.db.PerformanceHistoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class GameRepository(private val context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val gameDao = db.gameDao()
    private val performanceDao = db.performanceDao()

    val visibleGames: Flow<List<GameEntity>> = gameDao.getAllVisibleGames()
    val favoriteGames: Flow<List<GameEntity>> = gameDao.getFavoriteGames()
    val hiddenGames: Flow<List<GameEntity>> = gameDao.getHiddenGames()
    val boostHistory: Flow<List<PerformanceHistoryEntity>> = performanceDao.getBoostHistory()

    suspend fun initializePresetGamesIfEmpty() = withContext(Dispatchers.IO) {
        val count = gameDao.getGameByPackage("com.cyber.pulse")
        if (count == null) {
            val presets = listOf(
                GameEntity(
                    packageName = "com.cyber.pulse",
                    title = "Cyber Pulse 2077",
                    category = "Action Sci-Fi",
                    iconDrawableResName = "img_game_cyber_pulse",
                    isFavorite = true,
                    performanceModeName = "ULTRA_PERFORMANCE",
                    lastPlayedTimestamp = System.currentTimeMillis() - 1200000L,
                    totalTimePlayedSeconds = 14200L
                ),
                GameEntity(
                    packageName = "com.neon.racer",
                    title = "Neon Racer Overdrive",
                    category = "Cyber Racing",
                    iconDrawableResName = "img_game_neon_racer",
                    isFavorite = true,
                    performanceModeName = "PERFORMANCE",
                    lastPlayedTimestamp = System.currentTimeMillis() - 86400000L,
                    totalTimePlayedSeconds = 8900L
                ),
                GameEntity(
                    packageName = "com.shadow.strike",
                    title = "Shadow Strike Mech",
                    category = "Tactical Shooter",
                    iconDrawableResName = "img_game_shadow_strike",
                    isFavorite = false,
                    performanceModeName = "BALANCED",
                    lastPlayedTimestamp = System.currentTimeMillis() - 172800000L,
                    totalTimePlayedSeconds = 5400L
                ),
                GameEntity(
                    packageName = "com.garena.game.kgtw",
                    title = "Apex Legends Mobile",
                    category = "Battle Royale",
                    isFavorite = false,
                    performanceModeName = "PERFORMANCE",
                    lastPlayedTimestamp = System.currentTimeMillis() - 345600000L,
                    totalTimePlayedSeconds = 21000L
                ),
                GameEntity(
                    packageName = "com.miHoYo.GenshinImpact",
                    title = "Genshin Impact",
                    category = "Open World RPG",
                    isFavorite = true,
                    performanceModeName = "ULTRA_PERFORMANCE",
                    lastPlayedTimestamp = System.currentTimeMillis() - 432000000L,
                    totalTimePlayedSeconds = 38000L
                )
            )
            gameDao.insertAll(presets)
        }
    }

    suspend fun scanAndScanInstalledGames(): List<GameEntity> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val installedApps = pm.getInstalledApplications(0)
        val discoveredGames = mutableListOf<GameEntity>()

        for (app in installedApps) {
            // Check if app has game category or name contains game keywords
            val isGame = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                app.category == ApplicationInfo.CATEGORY_GAME || (app.flags and ApplicationInfo.FLAG_IS_GAME) != 0
            } else {
                (app.flags and ApplicationInfo.FLAG_IS_GAME) != 0
            }

            val appName = pm.getApplicationLabel(app).toString()
            if (isGame || appName.contains("Game", true) || appName.contains("Race", true) || appName.contains("Craft", true)) {
                val entity = GameEntity(
                    packageName = app.packageName,
                    title = appName,
                    category = "Installed Game"
                )
                gameDao.insertOrUpdate(entity)
                discoveredGames.add(entity)
            }
        }
        discoveredGames
    }

    suspend fun addCustomGame(packageName: String, title: String, category: String) = withContext(Dispatchers.IO) {
        val entity = GameEntity(
            packageName = packageName,
            title = title,
            category = category
        )
        gameDao.insertOrUpdate(entity)
    }

    suspend fun updateGameSettings(game: GameEntity) = withContext(Dispatchers.IO) {
        gameDao.update(game)
    }

    suspend fun toggleFavorite(packageName: String, isFavorite: Boolean) = withContext(Dispatchers.IO) {
        gameDao.setFavorite(packageName, isFavorite)
    }

    suspend fun toggleHidden(packageName: String, isHidden: Boolean) = withContext(Dispatchers.IO) {
        gameDao.setHidden(packageName, isHidden)
    }

    suspend fun removeGame(game: GameEntity) = withContext(Dispatchers.IO) {
        gameDao.delete(game)
    }

    fun launchGame(packageName: String): Boolean {
        return try {
            val pm = context.packageManager
            val intent = pm.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}

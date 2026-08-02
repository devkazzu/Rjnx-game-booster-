package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "rjnx_settings")

class DataStoreManager(private val context: Context) {

    companion object {
        val KEY_DEFAULT_MODE = stringPreferencesKey("default_mode")
        val KEY_AUTO_BOOST = booleanPreferencesKey("auto_boost")
        val KEY_OVERLAY_ENABLED = booleanPreferencesKey("overlay_enabled")
        val KEY_AUTO_DND = booleanPreferencesKey("auto_dnd")
        val KEY_NOTIFICATION_CLEANER = booleanPreferencesKey("notification_cleaner")
        val KEY_FPS_MONITOR = booleanPreferencesKey("fps_monitor")
        val KEY_ANIMATION_SPEED = floatPreferencesKey("animation_speed")
        val KEY_OVERLAY_X = intPreferencesKey("overlay_x")
        val KEY_OVERLAY_Y = intPreferencesKey("overlay_y")
    }

    val defaultMode: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_DEFAULT_MODE] ?: "BALANCED"
    }

    val autoBoostEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_AUTO_BOOST] ?: true
    }

    val overlayEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_OVERLAY_ENABLED] ?: true
    }

    val autoDndEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_AUTO_DND] ?: true
    }

    val notificationCleanerEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_NOTIFICATION_CLEANER] ?: true
    }

    val fpsMonitorEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_FPS_MONITOR] ?: true
    }

    val animationSpeed: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[KEY_ANIMATION_SPEED] ?: 1.0f
    }

    suspend fun setDefaultMode(mode: String) {
        context.dataStore.edit { prefs -> prefs[KEY_DEFAULT_MODE] = mode }
    }

    suspend fun setAutoBoostEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[KEY_AUTO_BOOST] = enabled }
    }

    suspend fun setOverlayEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[KEY_OVERLAY_ENABLED] = enabled }
    }

    suspend fun setAutoDndEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[KEY_AUTO_DND] = enabled }
    }

    suspend fun setNotificationCleanerEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[KEY_NOTIFICATION_CLEANER] = enabled }
    }

    suspend fun setFpsMonitorEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[KEY_FPS_MONITOR] = enabled }
    }

    suspend fun setAnimationSpeed(speed: Float) {
        context.dataStore.edit { prefs -> prefs[KEY_ANIMATION_SPEED] = speed }
    }

    suspend fun saveOverlayPosition(x: Int, y: Int) {
        context.dataStore.edit { prefs ->
            prefs[KEY_OVERLAY_X] = x
            prefs[KEY_OVERLAY_Y] = y
        }
    }
}

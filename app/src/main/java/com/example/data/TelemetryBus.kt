package com.example.data

import com.example.data.model.SystemStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Process-wide telemetry snapshot.
 *
 * The Activity-scoped [com.example.ui.viewmodel.MainViewModel] is not reachable from plain
 * services (floating overlay / gaming-mode foreground service), so the monitor publishes every
 * sample here as well. That keeps the HUD live while a game is in the foreground, and lets the
 * accessibility service feed the detected game back into the UI.
 */
object TelemetryBus {

    /** Package name + display title of the game currently in the foreground. */
    data class ActiveGame(val packageName: String, val title: String)

    private val _stats = MutableStateFlow(SystemStats())
    val stats: StateFlow<SystemStats> = _stats.asStateFlow()

    private val _gamingModeActive = MutableStateFlow(false)
    val gamingModeActive: StateFlow<Boolean> = _gamingModeActive.asStateFlow()

    private val _activeGame = MutableStateFlow<ActiveGame?>(null)
    val activeGame: StateFlow<ActiveGame?> = _activeGame.asStateFlow()

    fun publish(stats: SystemStats) {
        _stats.value = stats
    }

    fun setGamingModeActive(active: Boolean) {
        _gamingModeActive.value = active
    }

    fun setActiveGame(packageName: String, title: String) {
        _activeGame.value = ActiveGame(packageName, title)
    }

    fun clearActiveGame() {
        _activeGame.value = null
    }
}

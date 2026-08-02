package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.GameEntity
import com.example.data.db.PerformanceHistoryEntity
import com.example.data.model.BoostResult
import com.example.data.model.PerformanceMode
import com.example.data.model.SystemStats
import com.example.data.preferences.DataStoreManager
import com.example.data.repository.GameRepository
import com.example.data.repository.SystemMonitorRepository
import com.example.utils.BoostManager
import com.example.utils.NotificationCleanerManager
import com.example.utils.ScreenRecorderManager
import com.example.utils.SystemMonitorManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class GameDataTuple(
    val games: List<GameEntity>,
    val favorites: List<GameEntity>,
    val hidden: List<GameEntity>,
    val history: List<PerformanceHistoryEntity>
)

data class ControlStateTuple(
    val isBoosting: Boolean,
    val lastBoostResult: BoostResult?,
    val selectedMode: PerformanceMode,
    val overlayEnabled: Boolean,
    val dndEnabled: Boolean
)

data class MainUiState(
    val stats: SystemStats = SystemStats(),
    val games: List<GameEntity> = emptyList(),
    val favoriteGames: List<GameEntity> = emptyList(),
    val hiddenGames: List<GameEntity> = emptyList(),
    val boostHistory: List<PerformanceHistoryEntity> = emptyList(),
    val isBoosting: Boolean = false,
    val lastBoostResult: BoostResult? = null,
    val selectedMode: PerformanceMode = PerformanceMode.BALANCED,
    val overlayEnabled: Boolean = true,
    val dndEnabled: Boolean = false,
    val isRecording: Boolean = false,
    val recordingDurationSec: Long = 0L
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val gameRepository = GameRepository(application)
    private val systemMonitorManager = SystemMonitorManager(application)
    private val systemMonitorRepository = SystemMonitorRepository(systemMonitorManager)
    private val boostManager = BoostManager(application)
    private val dataStoreManager = DataStoreManager(application)
    private val notificationCleanerManager = NotificationCleanerManager(application)
    private val screenRecorderManager = ScreenRecorderManager(application)

    private val _isBoosting = MutableStateFlow(false)
    private val _lastBoostResult = MutableStateFlow<BoostResult?>(null)
    private val _selectedMode = MutableStateFlow(PerformanceMode.BALANCED)
    private val _dndEnabled = MutableStateFlow(false)

    private val gameDataFlow = combine(
        gameRepository.visibleGames,
        gameRepository.favoriteGames,
        gameRepository.hiddenGames,
        gameRepository.boostHistory
    ) { games, favorites, hidden, history ->
        GameDataTuple(games, favorites, hidden, history)
    }

    private val controlStateFlow = combine(
        _isBoosting,
        _lastBoostResult,
        _selectedMode,
        dataStoreManager.overlayEnabled,
        _dndEnabled
    ) { isBoosting, lastBoostResult, selectedMode, overlayEnabled, dndEnabled ->
        ControlStateTuple(isBoosting, lastBoostResult, selectedMode, overlayEnabled, dndEnabled)
    }

    private val recorderStateFlow = combine(
        screenRecorderManager.isRecording,
        screenRecorderManager.recordingDurationSeconds
    ) { isRecording, duration ->
        Pair(isRecording, duration)
    }

    val uiState: StateFlow<MainUiState> = combine(
        systemMonitorRepository.systemStats,
        gameDataFlow,
        controlStateFlow,
        recorderStateFlow
    ) { stats, gameData, control, recorder ->
        MainUiState(
            stats = stats.copy(activeMode = control.selectedMode),
            games = gameData.games,
            favoriteGames = gameData.favorites,
            hiddenGames = gameData.hidden,
            boostHistory = gameData.history,
            isBoosting = control.isBoosting,
            lastBoostResult = control.lastBoostResult,
            selectedMode = control.selectedMode,
            overlayEnabled = control.overlayEnabled,
            dndEnabled = control.dndEnabled,
            isRecording = recorder.first,
            recordingDurationSec = recorder.second
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainUiState()
    )

    init {
        systemMonitorRepository.startMonitoring(viewModelScope)
        viewModelScope.launch {
            gameRepository.initializePresetGamesIfEmpty()
        }
    }

    fun triggerOneTapBoost() {
        if (_isBoosting.value) return
        viewModelScope.launch {
            _isBoosting.value = true
            val result = boostManager.executeOneTapBoost(_selectedMode.value)
            _lastBoostResult.value = result
            _isBoosting.value = false
        }
    }

    fun dismissBoostResult() {
        _lastBoostResult.value = null
    }

    fun setPerformanceMode(mode: PerformanceMode) {
        _selectedMode.value = mode
    }

    fun toggleFavorite(packageName: String, currentFavorite: Boolean) {
        viewModelScope.launch {
            gameRepository.toggleFavorite(packageName, !currentFavorite)
        }
    }

    fun toggleHidden(packageName: String, currentHidden: Boolean) {
        viewModelScope.launch {
            gameRepository.toggleHidden(packageName, !currentHidden)
        }
    }

    fun launchGame(game: GameEntity) {
        viewModelScope.launch {
            gameRepository.launchGame(game.packageName)
        }
    }

    fun addGame(packageName: String, title: String, category: String) {
        viewModelScope.launch {
            gameRepository.addCustomGame(packageName, title, category)
        }
    }

    fun scanInstalledGames() {
        viewModelScope.launch {
            gameRepository.scanAndScanInstalledGames()
        }
    }

    fun updateGameSettings(game: GameEntity) {
        viewModelScope.launch {
            gameRepository.updateGameSettings(game)
        }
    }

    fun removeGame(game: GameEntity) {
        viewModelScope.launch {
            gameRepository.removeGame(game)
        }
    }

    fun toggleOverlay(current: Boolean) {
        viewModelScope.launch {
            dataStoreManager.setOverlayEnabled(!current)
        }
    }

    fun toggleDnd() {
        _dndEnabled.value = !_dndEnabled.value
        if (_dndEnabled.value) {
            notificationCleanerManager.enableGamingDnd()
        } else {
            notificationCleanerManager.disableGamingDnd()
        }
    }

    fun toggleRecording() {
        if (screenRecorderManager.isRecording.value) {
            screenRecorderManager.stopRecording()
        } else {
            screenRecorderManager.startRecording()
        }
    }

    override fun onCleared() {
        super.onCleared()
        systemMonitorRepository.stopMonitoring()
    }
}

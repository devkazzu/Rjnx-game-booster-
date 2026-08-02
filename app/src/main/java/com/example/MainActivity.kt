package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.db.GameEntity
import com.example.ui.components.CyberConsoleNavRail
import com.example.ui.components.CyberRightTelemetryDrawer
import com.example.ui.components.CyberTab
import com.example.ui.components.CyberTopHeader
import com.example.ui.screens.*
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.RJNXGameBoosterTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        setContent {
            RJNXGameBoosterTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                var showSplashScreen by remember { mutableStateOf(true) }
                var currentTab by remember { mutableStateOf(CyberTab.HOME) }
                var activeTuningGame by remember { mutableStateOf<GameEntity?>(null) }
                var isHudDrawerOpen by remember { mutableStateOf(false) }

                if (showSplashScreen) {
                    CyberSplashScreen(
                        onSplashFinished = { showSplashScreen = false }
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(CyberBlack)
                            .windowInsetsPadding(WindowInsets.systemBars)
                    ) {
                        // 1. Left Console Side Navigation Rail
                        if (activeTuningGame == null) {
                            CyberConsoleNavRail(
                                currentTab = currentTab,
                                onTabSelected = { currentTab = it },
                                onQuickBoostClick = { viewModel.triggerOneTapBoost() }
                            )
                        }

                        // 2. Central Display Area
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        ) {
                            // Top Status Ticker Header
                            CyberTopHeader(
                                systemStats = uiState.stats,
                                activeMode = uiState.selectedMode,
                                isHudDrawerOpen = isHudDrawerOpen,
                                onToggleHudDrawer = { isHudDrawerOpen = !isHudDrawerOpen }
                            )

                            // Main Console Display View
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {
                                if (activeTuningGame != null) {
                                    GameSettingsScreen(
                                        game = activeTuningGame!!,
                                        onSaveGame = { updated ->
                                            viewModel.updateGameSettings(updated)
                                            activeTuningGame = null
                                        },
                                        onBack = { activeTuningGame = null }
                                    )
                                } else {
                                    when (currentTab) {
                                        CyberTab.HOME -> {
                                            HomeScreen(
                                                uiState = uiState,
                                                onBoostClick = { viewModel.triggerOneTapBoost() },
                                                onDismissBoostResult = { viewModel.dismissBoostResult() },
                                                onSelectMode = { viewModel.setPerformanceMode(it) },
                                                onLaunchGame = { viewModel.launchGame(it) },
                                                onToggleFavorite = { pkg, fav -> viewModel.toggleFavorite(pkg, fav) },
                                                onToggleOverlay = { viewModel.toggleOverlay(uiState.overlayEnabled) },
                                                onToggleDnd = { viewModel.toggleDnd() },
                                                onToggleRecording = { viewModel.toggleRecording() }
                                            )
                                        }
                                        CyberTab.GAMES -> {
                                            GameLibraryScreen(
                                                uiState = uiState,
                                                onLaunchGame = { viewModel.launchGame(it) },
                                                onToggleFavorite = { pkg, fav -> viewModel.toggleFavorite(pkg, fav) },
                                                onToggleHidden = { pkg, hid -> viewModel.toggleHidden(pkg, hid) },
                                                onOpenGameSettings = { activeTuningGame = it },
                                                onScanInstalledGames = { viewModel.scanInstalledGames() },
                                                onAddCustomGame = { pkg, title, cat -> viewModel.addGame(pkg, title, cat) }
                                            )
                                        }
                                        CyberTab.PERFORMANCE -> {
                                            PerformanceScreen(
                                                uiState = uiState,
                                                onSelectMode = { viewModel.setPerformanceMode(it) }
                                            )
                                        }
                                        CyberTab.MONITOR -> {
                                            SystemMonitorScreen(
                                                uiState = uiState
                                            )
                                        }
                                        CyberTab.SETTINGS -> {
                                            SettingsScreen(
                                                uiState = uiState,
                                                onToggleOverlay = { viewModel.toggleOverlay(uiState.overlayEnabled) },
                                                onToggleDnd = { viewModel.toggleDnd() },
                                                onReplaySplash = { showSplashScreen = true }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 3. Right Telemetry Side Panel (Drawer)
                        CyberRightTelemetryDrawer(
                            isOpen = isHudDrawerOpen,
                            systemStats = uiState.stats,
                            overlayEnabled = uiState.overlayEnabled,
                            dndEnabled = uiState.dndEnabled,
                            isRecording = uiState.isRecording,
                            onClose = { isHudDrawerOpen = false },
                            onOneTapBoost = { viewModel.triggerOneTapBoost() },
                            onToggleOverlay = { viewModel.toggleOverlay(uiState.overlayEnabled) },
                            onToggleDnd = { viewModel.toggleDnd() },
                            onToggleRecording = { viewModel.toggleRecording() }
                        )
                    }
                }
            }
        }
    }
}

package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.db.GameEntity
import com.example.ui.components.CyberBottomBar
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
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(CyberBlack)
                    ) {
                        // 1. Primary 3D View Screen
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
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

                        // 2. Floating Top Gaming HUD Header
                        if (activeTuningGame == null) {
                            CyberTopHeader(
                                systemStats = uiState.stats,
                                activeMode = uiState.selectedMode,
                                isHudDrawerOpen = isHudDrawerOpen,
                                onToggleHudDrawer = { isHudDrawerOpen = !isHudDrawerOpen },
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .fillMaxWidth()
                            )
                        }

                        // 3. Floating Bottom Gaming Console Navigation Dock
                        if (activeTuningGame == null) {
                            CyberBottomBar(
                                currentTab = currentTab,
                                onTabSelected = { currentTab = it },
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth(0.65f)
                                    .padding(bottom = 6.dp)
                            )
                        }

                        // 4. Floating Right Telemetry HUD Drawer
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

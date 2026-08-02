package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.GameEntity
import com.example.data.model.BoostResult
import com.example.data.model.PerformanceMode
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainUiState

@Composable
fun HomeScreen(
    uiState: MainUiState,
    onBoostClick: () -> Unit,
    onDismissBoostResult: () -> Unit,
    onSelectMode: (PerformanceMode) -> Unit,
    onLaunchGame: (GameEntity) -> Unit,
    onToggleFavorite: (String, Boolean) -> Unit,
    onToggleOverlay: () -> Unit,
    onToggleDnd: () -> Unit,
    onToggleRecording: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showBoostDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.lastBoostResult) {
        if (uiState.lastBoostResult != null) {
            showBoostDialog = true
        }
    }

    Box(
        modifier = modifier
            .testTag("game_space_home_screen")
            .fillMaxSize()
            .background(CyberBlack)
    ) {
        // 1. Continuous 60 FPS Animated Cyber Background Canvas
        CyberBackgroundCanvas(modifier = Modifier.fillMaxSize())

        // 2. Main 3D Command Center View Layout (Full Landscape Scene)
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 52.dp, bottom = 60.dp, start = 12.dp, end = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // LEFT PANEL: Game Space Performance Engine Dock (~22% width)
            Column(
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "ENGINE MODES",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )

                // Mode Selector Buttons
                PerformanceMode.values().forEach { mode ->
                    val isSelected = mode == uiState.selectedMode
                    val modeColor = when (mode) {
                        PerformanceMode.ULTRA_PERFORMANCE -> NeonRed
                        PerformanceMode.PERFORMANCE -> NeonYellow
                        PerformanceMode.BALANCED -> NeonCyan
                        PerformanceMode.BATTERY_SAVER -> NeonGreen
                        PerformanceMode.CUSTOM -> CyberPurple
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) modeColor.copy(alpha = 0.25f) else CyberDarkSurface.copy(alpha = 0.8f))
                            .border(
                                width = if (isSelected) 1.5.dp else 0.8.dp,
                                color = if (isSelected) modeColor else CyberBorder,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { onSelectMode(mode) }
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = mode.name,
                                color = if (isSelected) Color.White else TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                                fontFamily = FontFamily.Monospace
                            )
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(modeColor)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "QUICK OS CONTROLS",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )

                // OS Quick Controls
                ControlPill(
                    label = "HUD OVERLAY",
                    active = uiState.overlayEnabled,
                    icon = Icons.Default.Layers,
                    onClick = onToggleOverlay
                )
                ControlPill(
                    label = "DND SHIELD",
                    active = uiState.dndEnabled,
                    icon = Icons.Default.DoNotDisturbOn,
                    onClick = onToggleDnd
                )
                ControlPill(
                    label = if (uiState.isRecording) "RECORDER ON" else "RECORDER",
                    active = uiState.isRecording,
                    icon = Icons.Default.Videocam,
                    onClick = onToggleRecording
                )
            }

            // CENTER SCENE: Holographic Boost Core & Cinematic Carousel (~53% width)
            Column(
                modifier = Modifier
                    .weight(2.1f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top: Central Animated Holographic Boost Core (Occupies ~40% Screen Focus)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.1f),
                    contentAlignment = Alignment.Center
                ) {
                    HolographicBoostEngineCore(
                        stats = uiState.stats,
                        isBoosting = uiState.isBoosting,
                        onBoostClick = onBoostClick
                    )
                }

                // Bottom: Cinematic Game Carousel
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    CinematicGameCarousel(
                        games = uiState.games,
                        onLaunchGame = onLaunchGame,
                        onToggleFavorite = onToggleFavorite
                    )
                }
            }

            // RIGHT PANEL: Live Telemetry HUD (~25% width)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "HARDWARE TELEMETRY",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )

                // Telemetry Card 1: CPU & FPS Gauges
                CyberGlowCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("CPU & RENDER HUD", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            CyberGauge(
                                title = "CPU LOAD",
                                value = uiState.stats.cpuUsagePercent.toFloat(),
                                maxValue = 100f,
                                unit = "%",
                                size = 65.dp
                            )
                            CyberGauge(
                                title = "TARGET FPS",
                                value = uiState.stats.currentFps.toFloat(),
                                maxValue = 120f,
                                unit = "FPS",
                                size = 65.dp
                            )
                        }
                    }
                }

                // Telemetry Card 2: Memory & Battery Thermals
                CyberGlowCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("MEMORY & THERMALS", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)

                        Text("RAM Footprint: ${uiState.stats.ramUsedMb} MB / ${uiState.stats.ramTotalMb} MB", color = TextSecondary, fontSize = 9.sp)
                        LinearProgressIndicator(
                            progress = { uiState.stats.ramUsagePercent / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = NeonYellow,
                            trackColor = CyberBorder
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("TEMP: ${uiState.stats.batteryTempC.toInt()}°C", color = if (uiState.stats.batteryTempC > 42) NeonRed else NeonGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            Text("PING: ${uiState.stats.pingMs} ms", color = NeonCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }

    // Boost Result Dialog Modal
    if (showBoostDialog && uiState.lastBoostResult != null) {
        BoostResultModal(
            result = uiState.lastBoostResult!!,
            onDismiss = {
                showBoostDialog = false
                onDismissBoostResult()
            }
        )
    }
}

@Composable
private fun ControlPill(
    label: String,
    active: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    val bg = if (active) NeonRed.copy(alpha = 0.2f) else CyberDarkSurface.copy(alpha = 0.8f)
    val border = if (active) NeonRed else CyberBorder
    val iconColor = if (active) NeonRed else TextMuted

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .border(0.8.dp, border, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = label, tint = iconColor, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = label, color = if (active) TextPrimary else TextSecondary, fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun BoostResultModal(
    result: BoostResult,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Bolt, contentDescription = null, tint = NeonRed, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("TURBO PURGE COMPLETE", color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 14.sp, fontWeight = FontWeight.Black)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Freed Memory: +${result.memoryFreedMb} MB RAM", color = NeonGreen, fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("CPU Optimization: +${result.cpuOptimizedPercent}% Speed", color = NeonCyan, fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("Background Apps Purged: ${result.appsClosedCount}", color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = NeonRed)
            ) {
                Text("ENGAGE HYPER BOOST", color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = CyberDarkSurface,
        titleContentColor = Color.White,
        textContentColor = TextSecondary
    )
}

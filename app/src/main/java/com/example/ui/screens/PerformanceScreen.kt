package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PerformanceMode
import com.example.ui.components.CyberGlowCard
import com.example.ui.components.CyberGraph
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainUiState

@Composable
fun PerformanceScreen(
    uiState: MainUiState,
    onSelectMode: (PerformanceMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val fpsPoints = remember(uiState.stats.currentFps) {
        mutableStateListOf(58f, 59f, 60f, 60f, 57f, 59f, 60f, uiState.stats.currentFps.toFloat())
    }

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBlack)
            .padding(top = 52.dp, bottom = 60.dp, start = 12.dp, end = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Left Column: Realtime FPS Telemetry Chart & Stats
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column {
                Text(
                    text = "PERFORMANCE ENGINE HUD",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Real-time framerate telemetry & governor tuning",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            CyberGlowCard(
                modifier = Modifier.fillMaxWidth(),
                glowColor = NeonCyan,
                isAnimated = true
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "REALTIME FPS TELEMETRY",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NeonCyan.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${uiState.stats.currentFps} FPS",
                                color = NeonCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Line Chart
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CyberCardBg)
                            .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
                            .padding(8.dp)
                    ) {
                        CyberGraph(
                            dataPoints = fpsPoints,
                            lineColor = NeonCyan,
                            fillColor = NeonCyan.copy(alpha = 0.2f),
                            minY = 30f,
                            maxY = 120f
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // FPS Metrics Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FpsMetric("AVG FPS", "${uiState.stats.averageFps}", NeonCyan)
                        FpsMetric("MAX FPS", "${uiState.stats.maxFps}", NeonGreen)
                        FpsMetric("MIN FPS", "${uiState.stats.minFps}", NeonRed)
                        FpsMetric("FRAME TIME", "${String.format("%.1f", uiState.stats.frameTimeMs)}ms", NeonYellow)
                    }
                }
            }
        }

        // Right Column: System Performance Modes Grid
        Column(
            modifier = Modifier
                .weight(1.1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "SELECT ENGINE PROFILES",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )

            PerformanceMode.values().forEach { mode ->
                val isSelected = mode == uiState.selectedMode
                val modeColor = Color(mode.colorHex)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) modeColor.copy(alpha = 0.15f) else CyberCardBg)
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) modeColor else CyberBorder,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onSelectMode(mode) }
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(modeColor)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = mode.title,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(modeColor.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${mode.targetFps} FPS",
                                    color = modeColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = mode.description,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(text = "Governor: ${mode.cpuGovernor}", color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                            Text(text = "Status: ${if (isSelected) "ACTIVE" else "READY"}", color = if (isSelected) modeColor else TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FpsMetric(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        Text(text = value, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
    }
}

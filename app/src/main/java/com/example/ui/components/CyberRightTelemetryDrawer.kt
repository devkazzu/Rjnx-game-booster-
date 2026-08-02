package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SystemStats
import com.example.ui.theme.*

@Composable
fun CyberRightTelemetryDrawer(
    isOpen: Boolean,
    systemStats: SystemStats,
    overlayEnabled: Boolean,
    dndEnabled: Boolean,
    isRecording: Boolean,
    onClose: () -> Unit,
    onOneTapBoost: () -> Unit,
    onToggleOverlay: () -> Unit,
    onToggleDnd: () -> Unit,
    onToggleRecording: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isOpen,
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it }),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(260.dp)
                .background(CyberDarkSurface.copy(alpha = 0.96f))
                .border(
                    width = (0.8).dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(NeonRed.copy(alpha = 0.6f), CyberBorder, NeonCyan.copy(alpha = 0.6f))
                    ),
                    shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                )
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Assessment,
                            contentDescription = "Telemetry",
                            tint = NeonRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "TELEMETRY DOCK",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(CyberBlack)
                            .border(1.dp, CyberBorder, CircleShape)
                            .clickable { onClose() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close HUD",
                            tint = TextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                // ONE-TAP TURBO BOOST HERO BUTTON
                Box(
                    modifier = Modifier
                        .testTag("drawer_boost_button")
                        .fillMaxWidth()
                        .height(72.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    NeonRed.copy(alpha = 0.35f),
                                    CyberBlack
                                )
                            )
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.horizontalGradient(listOf(NeonRed, NeonCyan)),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { onOneTapBoost() }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(NeonRed.copy(alpha = 0.2f))
                                .border(1.dp, NeonRed, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "Boost",
                                tint = NeonRed,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "TURBO PURGE",
                                color = NeonRed,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Clear RAM & Maximize CPU",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                // Live Gauge Cluster
                Text(
                    text = "HARDWARE TELEMETRY",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )

                // CPU Telemetry Bar
                TelemetryStatBar(
                    label = "CPU LOAD",
                    valueText = "${systemStats.cpuUsagePercent}%",
                    progress = systemStats.cpuUsagePercent / 100f,
                    color = if (systemStats.cpuUsagePercent > 85) NeonRed else NeonCyan
                )

                // RAM Telemetry Bar
                TelemetryStatBar(
                    label = "RAM USED",
                    valueText = "${systemStats.ramUsedMb} MB (${systemStats.ramUsagePercent}%)",
                    progress = systemStats.ramUsagePercent / 100f,
                    color = NeonYellow
                )

                // Thermal Battery Bar
                TelemetryStatBar(
                    label = "BATTERY TEMP",
                    valueText = "${systemStats.batteryTempC.toInt()}°C",
                    progress = (systemStats.batteryTempC / 60f).coerceIn(0f, 1f),
                    color = if (systemStats.batteryTempC > 42) NeonRed else NeonGreen
                )

                // Quick Controls Grid
                Text(
                    text = "QUICK OS CONTROLS",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickDrawerToggle(
                        label = "OVERLAY",
                        active = overlayEnabled,
                        icon = Icons.Default.Layers,
                        onClick = onToggleOverlay,
                        modifier = Modifier.weight(1f)
                    )
                    QuickDrawerToggle(
                        label = "DND MODE",
                        active = dndEnabled,
                        icon = Icons.Default.DoNotDisturbOn,
                        onClick = onToggleDnd,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickDrawerToggle(
                        label = if (isRecording) "REC ON" else "RECORDER",
                        active = isRecording,
                        icon = Icons.Default.Videocam,
                        onClick = onToggleRecording,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun TelemetryStatBar(
    label: String,
    valueText: String,
    progress: Float,
    color: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CyberBlack)
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = valueText,
                color = color,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = color,
            trackColor = CyberBorder
        )
    }
}

@Composable
private fun QuickDrawerToggle(
    label: String,
    active: Boolean,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (active) NeonRed.copy(alpha = 0.2f) else CyberBlack
    val border = if (active) NeonRed else CyberBorder
    val iconColor = if (active) NeonRed else TextMuted

    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = if (active) TextPrimary else TextSecondary,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

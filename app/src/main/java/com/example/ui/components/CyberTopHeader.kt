package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PerformanceMode
import com.example.data.model.SystemStats
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CyberTopHeader(
    systemStats: SystemStats,
    activeMode: PerformanceMode,
    isHudDrawerOpen: Boolean,
    onToggleHudDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTimeString by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        while (true) {
            currentTimeString = formatter.format(Date())
            kotlinx.coroutines.delay(1000)
        }
    }

    val modeColor = when (activeMode) {
        PerformanceMode.ULTRA_PERFORMANCE -> NeonRed
        PerformanceMode.PERFORMANCE -> NeonYellow
        PerformanceMode.BALANCED -> NeonCyan
        PerformanceMode.BATTERY_SAVER -> NeonGreen
        PerformanceMode.CUSTOM -> CyberPurple
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(CyberDarkSurface.copy(alpha = 0.95f))
            .border(
                width = (0.8).dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        NeonCyan.copy(alpha = 0.3f),
                        CyberBorder,
                        NeonRed.copy(alpha = 0.3f)
                    )
                ),
                shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Status Badges
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // System Status Tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CyberBlack)
                        .border(0.8.dp, CyberBorder, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(NeonGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SYSTEM ONLINE",
                            color = TextPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Active Performance Mode Tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(modeColor.copy(alpha = 0.15f))
                        .border(1.dp, modeColor.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "MODE: ${activeMode.name}",
                        color = modeColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Quick Hardware Telemetry Ticker
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = "FPS: ${systemStats.currentFps}",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "CPU: ${systemStats.cpuUsagePercent}%",
                        color = if (systemStats.cpuUsagePercent > 85) NeonRed else TextSecondary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "RAM: ${systemStats.ramUsagePercent}%",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "TEMP: ${systemStats.batteryTempC.toInt()}°C",
                        color = if (systemStats.batteryTempC > 42) NeonRed else NeonCyan,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Right Status Bar Items (Time, Network, Battery, HUD Drawer Toggle)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Latency Ping
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = "Wi-Fi",
                        tint = NeonGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${systemStats.pingMs}ms",
                        color = NeonGreen,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Battery Status
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.BatteryChargingFull,
                        contentDescription = "Battery",
                        tint = if (systemStats.batteryPercent < 20) NeonRed else NeonCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${systemStats.batteryPercent}%",
                        color = TextPrimary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                // System Clock
                Text(
                    text = currentTimeString,
                    color = NeonYellow,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                // HUD Drawer Toggle Button
                val drawerBgColor by animateColorAsState(
                    targetValue = if (isHudDrawerOpen) NeonRed.copy(alpha = 0.3f) else CyberBlack,
                    label = "drawer_bg"
                )

                Box(
                    modifier = Modifier
                        .testTag("toggle_hud_drawer_button")
                        .clip(RoundedCornerShape(6.dp))
                        .background(drawerBgColor)
                        .border(
                            width = 1.dp,
                            color = if (isHudDrawerOpen) NeonRed else CyberBorder,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .clickable { onToggleHudDrawer() }
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "HUD",
                            tint = if (isHudDrawerOpen) NeonRed else NeonCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isHudDrawerOpen) "HUD ON" else "HUD DOCK",
                            color = if (isHudDrawerOpen) NeonRed else TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

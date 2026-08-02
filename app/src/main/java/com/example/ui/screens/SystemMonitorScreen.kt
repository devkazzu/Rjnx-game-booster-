package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.PerformanceHistoryEntity
import com.example.ui.components.CyberGlowCard
import com.example.ui.components.CyberGraph
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainUiState

@Composable
fun SystemMonitorScreen(
    uiState: MainUiState,
    modifier: Modifier = Modifier
) {
    val networkHistory = remember(uiState.stats.downloadSpeedKbps) {
        listOf(240f, 450f, 890f, 1200f, 650f, 1100f, uiState.stats.downloadSpeedKbps)
    }

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBlack)
            .padding(top = 52.dp, bottom = 60.dp, start = 12.dp, end = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Left Column: Network & Memory
        Column(
            modifier = Modifier
                .weight(1.1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column {
                Text(
                    text = "SYSTEM HARDWARE TELEMETRY",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Real-time hardware sensors & network bandwidth monitor",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Network Card
            CyberGlowCard(
                modifier = Modifier.fillMaxWidth(),
                glowColor = NeonCyan
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Wifi, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("NETWORK BANDWIDTH", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                        Text("${uiState.stats.pingMs} ms PING", color = if (uiState.stats.pingMs < 40) NeonGreen else NeonYellow, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        NetMetric("DOWN", "${String.format("%.1f", uiState.stats.downloadSpeedKbps / 1024.0)} MB/s", NeonCyan)
                        NetMetric("UP", "${String.format("%.1f", uiState.stats.uploadSpeedKbps / 1024.0)} MB/s", NeonRed)
                        NetMetric("LOSS", "${uiState.stats.packetLossPercent}%", NeonGreen)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberCardBg)
                            .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                            .padding(6.dp)
                    ) {
                        CyberGraph(
                            dataPoints = networkHistory,
                            lineColor = NeonCyan,
                            fillColor = NeonCyan.copy(alpha = 0.15f),
                            minY = 0f,
                            maxY = 2500f
                        )
                    }
                }
            }

            // Memory Allocation Card
            CyberGlowCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text("MEMORY & STORAGE FOOTPRINT", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("RAM Footprint: ${uiState.stats.ramUsedMb} MB / ${uiState.stats.ramTotalMb} MB (${uiState.stats.ramUsagePercent}%)", color = TextSecondary, fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(CyberCardBg)
                            .border(1.dp, CyberBorder, RoundedCornerShape(5.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(uiState.stats.ramUsagePercent / 100f)
                                .background(NeonCyan)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Storage Footprint: ${uiState.stats.storageUsedGb} GB / ${uiState.stats.storageTotalGb} GB (${uiState.stats.storageUsagePercent}%)", color = TextSecondary, fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(CyberCardBg)
                            .border(1.dp, CyberBorder, RoundedCornerShape(5.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(uiState.stats.storageUsagePercent / 100f)
                                .background(NeonYellow)
                        )
                    }
                }
            }
        }

        // Right Column: Optimization History Log
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("OPTIMIZATION LOG HISTORY", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)

            if (uiState.boostHistory.isEmpty()) {
                CyberGlowCard(modifier = Modifier.fillMaxWidth()) {
                    Text("No boost history recorded yet. Tap ONE TAP BOOST to log.", color = TextMuted, fontSize = 11.sp)
                }
            } else {
                uiState.boostHistory.forEach { item ->
                    BoostHistoryRow(item)
                }
            }
        }
    }
}

@Composable
fun NetMetric(label: String, value: String, color: Color) {
    Column {
        Text(text = label, color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        Text(text = value, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
    }
}

@Composable
fun BoostHistoryRow(item: PerformanceHistoryEntity) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CyberCardBg)
            .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "BOOST EVENT (${item.modeName})", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                Text(text = "Freed: +${item.memoryFreedMb} MB RAM • Apps: ${item.appsClosedCount}", color = TextSecondary, fontSize = 10.sp)
            }
            Text(text = "+${item.cpuOptimizedPercent}% CPU", color = NeonRed, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }
    }
}

package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CyberGlowCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainUiState

@Composable
fun SettingsScreen(
    uiState: MainUiState,
    onToggleOverlay: () -> Unit,
    onToggleDnd: () -> Unit,
    onReplaySplash: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var animationSpeed by remember { mutableFloatStateOf(1.0f) }

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBlack)
            .padding(top = 52.dp, bottom = 60.dp, start = 12.dp, end = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Left Column: Permissions Diagnostic Card
        Column(
            modifier = Modifier
                .weight(1.1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column {
                Text(
                    text = "RJNX ENGINE CONFIGURATION",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "System permissions & overlay telemetry preferences",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Permissions Status Card
            CyberGlowCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("SYSTEM PERMISSIONS DIAGNOSTIC", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)

                    PermissionStatusRow("Display Over Other Apps (Overlay)", Settings.canDrawOverlays(context)) {
                        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))
                        context.startActivity(intent)
                    }

                    PermissionStatusRow("Do Not Disturb Access", uiState.dndEnabled) {
                        val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                        context.startActivity(intent)
                    }

                    PermissionStatusRow("Accessibility Game Detector Service", true) {
                        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                        context.startActivity(intent)
                    }
                }
            }
        }

        // Right Column: Preferences & About
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Animation & UI Preferences
            CyberGlowCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("HUD ANIMATION REFRESH", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)

                    Text("Animation Speed (${String.format("%.1f", animationSpeed)}x)", color = TextSecondary, fontSize = 11.sp)
                    Slider(
                        value = animationSpeed,
                        onValueChange = { animationSpeed = it },
                        valueRange = 0.5f..2.0f,
                        steps = 2,
                        colors = SliderDefaults.colors(thumbColor = NeonRed, activeTrackColor = NeonRed)
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = onReplaySplash,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("REPLAY CYBER INTRO INTRO ANIMATION", color = NeonCyan, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // About & Version
            CyberGlowCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("ABOUT RJNX GAME BOOSTER", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    Text("Version: 3.2.0 Cyber Edition (Build 1004)", color = TextSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Text("Engine: Kotlin + Jetpack Compose + Room + Coroutines", color = TextMuted, fontSize = 9.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Inspired by high-performance gaming hardware dashboards.", color = TextMuted, fontSize = 9.sp)
                }
            }
        }
    }
}

@Composable
fun PermissionStatusRow(title: String, isGranted: Boolean, onGrant: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            Text(text = if (isGranted) "GRANTED" else "ACTION REQUIRED", color = if (isGranted) NeonGreen else NeonRed, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }
        if (!isGranted) {
            Button(
                onClick = onGrant,
                colors = ButtonDefaults.buttonColors(containerColor = NeonRed.copy(alpha = 0.2f)),
                modifier = Modifier.padding(start = 6.dp)
            ) {
                Text("ENABLE", color = NeonRed, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

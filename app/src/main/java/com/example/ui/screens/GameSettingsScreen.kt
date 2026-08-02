package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.GameEntity
import com.example.data.model.PerformanceMode
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberGlowCard
import com.example.ui.theme.*

@Composable
fun GameSettingsScreen(
    game: GameEntity,
    onSaveGame: (GameEntity) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var modeName by remember { mutableStateOf(game.performanceModeName) }
    var autoDnd by remember { mutableStateOf(game.autoDndEnabled) }
    var touchOpt by remember { mutableStateOf(game.touchOptimization) }
    var netPriority by remember { mutableStateOf(game.networkPriority) }
    var overlayEnabled by remember { mutableStateOf(game.overlayEnabled) }
    var brightness by remember { mutableFloatStateOf(game.customBrightnessPercent / 100f) }
    var volume by remember { mutableFloatStateOf(game.customVolumePercent / 100f) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBlack)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PER-GAME ENGINE TUNER",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                TextButton(onClick = onBack) {
                    Text("BACK", color = TextMuted)
                }
            }
        }

        item {
            CyberGlowCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(text = game.title.uppercase(), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    Text(text = game.packageName, color = TextMuted, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                }
            }
        }

        item {
            CyberGlowCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("PERFORMANCE MODE ASSIGNED", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)

                    PerformanceMode.values().forEach { mode ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (mode.name == modeName),
                                onClick = { modeName = mode.name },
                                colors = RadioButtonDefaults.colors(selectedColor = NeonRed)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = mode.title, color = Color.White, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }

        item {
            CyberGlowCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("GAMING AUTOMATIONS", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)

                    SettingSwitchRow("Auto Silence Notifications (DND)", autoDnd) { autoDnd = it }
                    SettingSwitchRow("Touch Latency Optimization", touchOpt) { touchOpt = it }
                    SettingSwitchRow("Packet Network Priority", netPriority) { netPriority = it }
                    SettingSwitchRow("Floating Cyber Overlay HUD", overlayEnabled) { overlayEnabled = it }
                }
            }
        }

        item {
            CyberGlowCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("HARDWARE LOCK PRESETS", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)

                    Text("Screen Brightness Lock (${(brightness * 100).toInt()}%)", color = TextSecondary, fontSize = 12.sp)
                    Slider(
                        value = brightness,
                        onValueChange = { brightness = it },
                        colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                    )

                    Text("Game Audio Volume Lock (${(volume * 100).toInt()}%)", color = TextSecondary, fontSize = 12.sp)
                    Slider(
                        value = volume,
                        onValueChange = { volume = it },
                        colors = SliderDefaults.colors(thumbColor = NeonRed, activeTrackColor = NeonRed)
                    )
                }
            }
        }

        item {
            CyberButton(
                text = "SAVE PROFILE TUNING",
                onClick = {
                    val updated = game.copy(
                        performanceModeName = modeName,
                        autoDndEnabled = autoDnd,
                        touchOptimization = touchOpt,
                        networkPriority = netPriority,
                        overlayEnabled = overlayEnabled,
                        customBrightnessPercent = (brightness * 100).toInt(),
                        customVolumePercent = (volume * 100).toInt()
                    )
                    onSaveGame(updated)
                    onBack()
                },
                glowColor = NeonGreen,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun SettingSwitchRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, color = TextSecondary, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = NeonCyan.copy(alpha = 0.3f))
        )
    }
}

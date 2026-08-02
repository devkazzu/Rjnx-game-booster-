package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextMuted

enum class CyberTab(
    val title: String,
    val icon: ImageVector,
    val testTag: String
) {
    HOME("Home", Icons.Default.Speed, "nav_tab_home"),
    GAMES("Games", Icons.Default.SportsEsports, "nav_tab_games"),
    PERFORMANCE("Modes", Icons.Default.Tune, "nav_tab_modes"),
    MONITOR("System", Icons.Default.Memory, "nav_tab_system"),
    SETTINGS("Settings", Icons.Default.Settings, "nav_tab_settings")
}

@Composable
fun CyberBottomBar(
    currentTab: CyberTab,
    onTabSelected: (CyberTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(CyberDarkSurface.copy(alpha = 0.95f))
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(NeonRed.copy(alpha = 0.5f), CyberBorder, NeonCyan.copy(alpha = 0.5f))
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(vertical = 6.dp, horizontal = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CyberTab.values().forEach { tab ->
                val selected = tab == currentTab
                val tintColor by animateColorAsState(
                    targetValue = if (selected) NeonRed else TextMuted,
                    animationSpec = spring(),
                    label = "tab_color"
                )

                Box(
                    modifier = Modifier
                        .testTag(tab.testTag)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (selected) NeonRed.copy(alpha = 0.15f) else Color.Transparent)
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.title,
                            tint = tintColor,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = tab.title,
                            color = tintColor,
                            fontSize = 10.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

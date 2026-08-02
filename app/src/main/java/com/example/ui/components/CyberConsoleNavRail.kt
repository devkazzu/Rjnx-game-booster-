package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import com.example.ui.theme.*

@Composable
fun CyberConsoleNavRail(
    currentTab: CyberTab,
    onTabSelected: (CyberTab) -> Unit,
    onQuickBoostClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "nav_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(84.dp)
            .background(CyberDarkSurface.copy(alpha = 0.95f))
            .border(
                width = (0.8).dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        NeonRed.copy(alpha = 0.6f),
                        CyberBorder,
                        NeonCyan.copy(alpha = 0.6f)
                    )
                ),
                shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
            )
            .padding(vertical = 12.dp, horizontal = 6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Logo Badge
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    NeonRed.copy(alpha = 0.3f),
                                    NeonCyan.copy(alpha = 0.3f)
                                )
                            )
                        )
                        .border(
                            width = 1.dp,
                            color = NeonCyan.copy(alpha = glowAlpha),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = "RJNX OS Logo",
                        tint = NeonCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "RJNX",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "OS v3.2",
                    color = NeonCyan.copy(alpha = 0.8f),
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Central Navigation Tabs
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                CyberTab.values().forEach { tab ->
                    val selected = tab == currentTab
                    val itemBgColor by animateColorAsState(
                        targetValue = if (selected) NeonRed.copy(alpha = 0.2f) else Color.Transparent,
                        animationSpec = spring(),
                        label = "tab_bg"
                    )
                    val iconTint by animateColorAsState(
                        targetValue = if (selected) NeonRed else TextMuted,
                        animationSpec = spring(),
                        label = "tab_tint"
                    )

                    Box(
                        modifier = Modifier
                            .testTag(tab.testTag)
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(itemBgColor)
                            .border(
                                width = if (selected) 1.dp else 0.dp,
                                color = if (selected) NeonRed.copy(alpha = 0.6f) else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onTabSelected(tab) },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left Active Line Indicator
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .fillMaxHeight(0.6f)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(if (selected) NeonRed else Color.Transparent)
                            )

                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.title,
                                    tint = iconTint,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = tab.title,
                                    color = iconTint,
                                    fontSize = 9.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            // Bottom One-Tap Quick Boost Action Button
            Box(
                modifier = Modifier
                    .testTag("console_nav_quick_boost")
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                NeonRed.copy(alpha = 0.4f),
                                CyberBlack
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.sweepGradient(
                            colors = listOf(NeonRed, NeonCyan, NeonRed)
                        ),
                        shape = CircleShape
                    )
                    .clickable { onQuickBoostClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "Quick Boost",
                    tint = NeonRed,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

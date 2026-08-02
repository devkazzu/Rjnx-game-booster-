package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SystemStats
import com.example.ui.theme.*

@Composable
fun HolographicBoostEngineCore(
    stats: SystemStats,
    isBoosting: Boolean,
    onBoostClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "core_anim")

    // Rotation angle 1 (Outer Ring)
    val outerRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "outer_rot"
    )

    // Rotation angle 2 (Inner Ring - Reverse)
    val innerRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "inner_rot"
    )

    // Core energy pulsing aura
    val corePulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val coreGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Box(
        modifier = modifier
            .testTag("holographic_boost_core")
            .size(220.dp),
        contentAlignment = Alignment.Center
    ) {
        // 1. 3D Canvas Multi-Layer Holographic Rotating RGB Arcs
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)

            // Outer Arc Ring
            val outerR = size.width * 0.46f
            rotate(outerRotation, center) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(NeonRed, NeonCyan, NeonYellow, NeonRed)
                    ),
                    startAngle = 0f,
                    sweepAngle = 140f,
                    useCenter = false,
                    topLeft = Offset(center.x - outerR, center.y - outerR),
                    size = Size(outerR * 2, outerR * 2),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                drawArc(
                    color = NeonCyan.copy(alpha = 0.5f),
                    startAngle = 190f,
                    sweepAngle = 100f,
                    useCenter = false,
                    topLeft = Offset(center.x - outerR, center.y - outerR),
                    size = Size(outerR * 2, outerR * 2),
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // Middle Arc Ring (Reverse)
            val midR = size.width * 0.38f
            rotate(innerRotation, center) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(NeonCyan, CyberPurple, NeonGreen, NeonCyan)
                    ),
                    startAngle = 45f,
                    sweepAngle = 160f,
                    useCenter = false,
                    topLeft = Offset(center.x - midR, center.y - midR),
                    size = Size(midR * 2, midR * 2),
                    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // Radial Energy Glow Field
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        NeonRed.copy(alpha = coreGlowAlpha * 0.45f),
                        NeonCyan.copy(alpha = coreGlowAlpha * 0.2f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = midR
                ),
                radius = midR,
                center = center
            )
        }

        // 2. Interactive Central Reactor Sphere
        Box(
            modifier = Modifier
                .size(130.dp)
                .scale(if (isBoosting) 1.15f else corePulse)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            NeonRed.copy(alpha = 0.5f),
                            CyberDarkSurface,
                            CyberBlack
                        )
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush.sweepGradient(listOf(NeonRed, NeonCyan, NeonRed)),
                    shape = CircleShape
                )
                .clickable { onBoostClick() }
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "Turbo Purge",
                    tint = if (isBoosting) NeonYellow else NeonRed,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isBoosting) "PURGING..." else "BOOST CORE",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "TAP TO OPTIMIZE",
                    color = NeonCyan,
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // 3. Floating Quick Status Pills Around Core
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-6).dp)
                .clip(RoundedCornerShape(6.dp))
                .background(CyberBlack.copy(alpha = 0.85f))
                .border(0.8.dp, NeonCyan, RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = "${stats.currentFps} FPS STABLE",
                color = NeonCyan,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 6.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(CyberBlack.copy(alpha = 0.85f))
                .border(0.8.dp, NeonRed, RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = "RAM: ${stats.ramUsagePercent}% USED",
                color = NeonRed,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

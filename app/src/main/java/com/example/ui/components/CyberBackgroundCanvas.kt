package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonRed
import kotlin.math.sin

private data class CyberParticle(
    var x: Float,
    var y: Float,
    val speed: Float,
    val radius: Float,
    val alphaOffset: Float
)

@Composable
fun CyberBackgroundCanvas(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cyber_bg")
    val animTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    val scanLineProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanline"
    )

    // Pre-allocated particle state for high performance 60FPS background rendering
    val particles = remember {
        List(35) {
            CyberParticle(
                x = (0..1000).random() / 1000f,
                y = (0..1000).random() / 1000f,
                speed = 0.0005f + (0..10).random() * 0.0002f,
                radius = 1.5f + (0..10).random() * 0.25f,
                alphaOffset = (0..100).random() / 100f
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // 1. Deep Cyber Radial Background Base
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    CyberBlack,
                    Color(0xFF030710),
                    Color(0xFF000308)
                ),
                center = Offset(width / 2f, height / 2f),
                radius = width.coerceAtLeast(height) * 0.8f
            )
        )

        // 2. Futuristic Grid Scan Pattern
        val gridStep = 45.dp.toPx()
        val gridAlpha = 0.12f
        val gridColor = CyberBorder.copy(alpha = gridAlpha)

        val colCount = (width / gridStep).toInt() + 1
        val rowCount = (height / gridStep).toInt() + 1

        for (i in 0..colCount) {
            val x = i * gridStep
            drawLine(
                color = gridColor,
                start = Offset(x, 0f),
                end = Offset(x, height),
                strokeWidth = 1f
            )
        }
        for (j in 0..rowCount) {
            val y = j * gridStep
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
        }

        // 3. Laser Sweep Line
        val sweepY = scanLineProgress * height
        drawLine(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, NeonCyan.copy(alpha = 0.4f), Color.Transparent),
                startY = (sweepY - 20f).coerceAtLeast(0f),
                endY = (sweepY + 20f).coerceAtMost(height)
            ),
            start = Offset(0f, sweepY),
            end = Offset(width, sweepY),
            strokeWidth = 2f
        )

        // 4. Floating Animated Particles
        particles.forEach { p ->
            p.y -= p.speed
            if (p.y < 0f) p.y = 1f

            val pX = p.x * width
            val pY = p.y * height
            val alpha = (sin((animTime * 0.05f + p.alphaOffset * 10f).toDouble()).toFloat() * 0.35f + 0.55f).coerceIn(0.1f, 0.9f)
            val pColor = if (p.radius > 2.5f) NeonRed.copy(alpha = alpha) else NeonCyan.copy(alpha = alpha)

            drawCircle(
                color = pColor,
                radius = p.radius.dp.toPx(),
                center = Offset(pX, pY)
            )
        }

        // 5. Corner Ambient Neon Glows
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(NeonRed.copy(alpha = 0.15f), Color.Transparent),
                center = Offset(0f, 0f),
                radius = 250.dp.toPx()
            ),
            radius = 250.dp.toPx(),
            center = Offset(0f, 0f)
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(NeonCyan.copy(alpha = 0.15f), Color.Transparent),
                center = Offset(width, height),
                radius = 250.dp.toPx()
            ),
            radius = 250.dp.toPx(),
            center = Offset(width, height)
        )
    }
}

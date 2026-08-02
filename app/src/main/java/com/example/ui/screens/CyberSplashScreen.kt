package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CyberSplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var progress by remember { mutableFloatStateOf(0f) }
    var bootStatusText by remember { mutableStateOf("INITIALIZING CORE DRIVERS...") }

    // 60 FPS continuous animation transition
    val infiniteTransition = rememberInfiniteTransition(label = "splash_loop")
    val rotationDegrees by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val reverseRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "reverse_rotation"
    )

    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val scanLineY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanline"
    )

    // Boot progress sequence simulation
    LaunchedEffect(Unit) {
        val startTime = System.currentTimeMillis()
        val duration = 2400L
        while (true) {
            val elapsed = System.currentTimeMillis() - startTime
            val currentProg = (elapsed.toFloat() / duration).coerceIn(0f, 1f)
            progress = currentProg

            bootStatusText = when {
                currentProg < 0.25f -> "[01/04] INITIALIZING KERNEL HARDWARE DRIVERS..."
                currentProg < 0.50f -> "[02/04] CALIBRATING CPU FREQUENCY GOVERNORS..."
                currentProg < 0.75f -> "[03/04] ALLOCATING HIGH-PRIORITY GPU BUFFER..."
                currentProg < 1.0f -> "[04/04] ENGAGING TURBO BOOST ENGINE..."
                else -> "SYSTEM ONLINE • LAUNCHING RJNX OS v3.2"
            }

            if (currentProg >= 1f) {
                delay(200)
                onSplashFinished()
                break
            }
            delay(16) // ~60 FPS update
        }
    }

    Box(
        modifier = modifier
            .testTag("cyber_splash_screen")
            .fillMaxSize()
            .background(CyberBlack)
    ) {
        // High Performance Canvas Render
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val center = Offset(canvasWidth / 2f, canvasHeight / 2f)

            // 1. Cyber Grid Background with Animated Glow
            val gridSpacing = 40.dp.toPx()
            val gridColor = CyberBorder.copy(alpha = 0.35f)
            val lineCountX = (canvasWidth / gridSpacing).toInt() + 1
            val lineCountY = (canvasHeight / gridSpacing).toInt() + 1

            for (i in 0..lineCountX) {
                val x = i * gridSpacing
                drawLine(
                    color = gridColor,
                    start = Offset(x, 0f),
                    end = Offset(x, canvasHeight),
                    strokeWidth = 1f
                )
            }
            for (j in 0..lineCountY) {
                val y = j * gridSpacing
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(canvasWidth, y),
                    strokeWidth = 1f
                )
            }

            // Animated Laser Scanning Line
            val scanY = scanLineY * canvasHeight
            drawLine(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, NeonCyan.copy(alpha = 0.7f), Color.Transparent),
                    startY = scanY - 30f,
                    endY = scanY + 30f
                ),
                start = Offset(0f, scanY),
                end = Offset(canvasWidth, scanY),
                strokeWidth = 3f
            )

            // 2. Animated Particle Energy Sparks
            val particleCount = 20
            for (p in 0 until particleCount) {
                val angle = (p * (360f / particleCount) + rotationDegrees) * (Math.PI / 180f).toFloat()
                val distance = 140.dp.toPx() + (p % 4 * 15.dp.toPx()) * sin(rotationDegrees * 0.05f + p)
                val pX = center.x + cos(angle.toDouble()).toFloat() * distance
                val pY = center.y + sin(angle.toDouble()).toFloat() * distance
                val pRadius = (2.dp.toPx() + (p % 3).dp.toPx())

                drawCircle(
                    color = if (p % 2 == 0) NeonCyan.copy(alpha = pulseGlow * 0.8f) else NeonRed.copy(alpha = pulseGlow * 0.8f),
                    radius = pRadius,
                    center = Offset(pX, pY)
                )
            }

            // 3. Canvas Outer Cyber HUD Ring
            val outerRadius = 120.dp.toPx()
            rotate(rotationDegrees, center) {
                drawCircle(
                    color = NeonCyan.copy(alpha = 0.3f),
                    radius = outerRadius,
                    center = center,
                    style = Stroke(width = 2f)
                )

                // Dashed HUD Arcs
                drawArc(
                    color = NeonCyan.copy(alpha = pulseGlow),
                    startAngle = 0f,
                    sweepAngle = 60f,
                    useCenter = false,
                    topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
                    size = Size(outerRadius * 2, outerRadius * 2),
                    style = Stroke(width = 4f, cap = StrokeCap.Round)
                )

                drawArc(
                    color = NeonRed.copy(alpha = pulseGlow),
                    startAngle = 180f,
                    sweepAngle = 75f,
                    useCenter = false,
                    topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
                    size = Size(outerRadius * 2, outerRadius * 2),
                    style = Stroke(width = 4f, cap = StrokeCap.Round)
                )
            }

            // 4. Reverse Rotating Hexagon Tech Shield
            val hexRadius = 90.dp.toPx()
            rotate(reverseRotation, center) {
                val hexPath = Path()
                for (i in 0 until 6) {
                    val angleRad = (i * 60f) * (Math.PI / 180f).toFloat()
                    val x = center.x + hexRadius * cos(angleRad.toDouble()).toFloat()
                    val y = center.y + hexRadius * sin(angleRad.toDouble()).toFloat()
                    if (i == 0) hexPath.moveTo(x, y) else hexPath.lineTo(x, y)
                }
                hexPath.close()

                drawPath(
                    path = hexPath,
                    color = NeonCyan.copy(alpha = 0.2f)
                )
                drawPath(
                    path = hexPath,
                    color = NeonCyan.copy(alpha = 0.8f),
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            // 5. Central Glowing Shield Logo Core
            val innerRadius = 60.dp.toPx()
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        NeonRed.copy(alpha = pulseGlow * 0.6f),
                        NeonCyan.copy(alpha = pulseGlow * 0.3f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = innerRadius * 1.5f
                ),
                radius = innerRadius * 1.5f,
                center = center
            )
        }

        // Foreground UI Overlay (Brand Title & Boot Status Ticker)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top HUD Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(NeonGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "RJNX HIGH-PERFORMANCE ENGINE",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }

                // Skip Button
                Box(
                    modifier = Modifier
                        .testTag("skip_splash_button")
                        .clip(RoundedCornerShape(6.dp))
                        .background(CyberDarkSurface)
                        .border(1.dp, CyberBorder, RoundedCornerShape(6.dp))
                        .clickable { onSplashFinished() }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "SKIP >>",
                        color = NeonCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Central Branding Text underneath the Canvas emblem
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 180.dp)
            ) {
                Text(
                    text = "RJNX OS",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 4.sp
                )
                Text(
                    text = "CYBER ENGINE v3.2",
                    color = NeonCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp
                )
            }

            // Bottom Boot Telemetry & Progress Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = bootStatusText,
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        color = NeonYellow,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // High-tech Segmented Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(CyberDarkSurface)
                        .border(1.dp, CyberBorder, RoundedCornerShape(4.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress.coerceIn(0f, 1f))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(NeonRed, NeonCyan, NeonGreen)
                                )
                            )
                    )
                }
            }
        }
    }
}

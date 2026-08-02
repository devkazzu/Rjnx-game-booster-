package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonRed

@Composable
fun CyberGraph(
    dataPoints: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color = NeonRed,
    fillColor: Color = NeonRed.copy(alpha = 0.2f),
    minY: Float = 0f,
    maxY: Float = 100f
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        if (width <= 0 || height <= 0) return@Canvas

        // Draw background grid lines
        val gridStepY = height / 4f
        for (i in 1..3) {
            drawLine(
                color = CyberBorder.copy(alpha = 0.5f),
                start = Offset(0f, gridStepY * i),
                end = Offset(width, gridStepY * i),
                strokeWidth = 1.dp.toPx()
            )
        }

        if (dataPoints.isEmpty()) return@Canvas

        val rangeY = if (maxY > minY) maxY - minY else 1f
        val stepX = if (dataPoints.size > 1) width / (dataPoints.size - 1) else width

        val path = Path()
        val fillPath = Path()

        dataPoints.forEachIndexed { index, value ->
            val normalizedY = ((value - minY) / rangeY).coerceIn(0f, 1f)
            val x = index * stepX
            val y = height - (normalizedY * height)

            if (index == 0) {
                path.moveTo(x, y)
                fillPath.moveTo(x, height)
                fillPath.lineTo(x, y)
            } else {
                path.lineTo(x, y)
                fillPath.lineTo(x, y)
            }

            if (index == dataPoints.lastIndex) {
                fillPath.lineTo(x, height)
                fillPath.close()
            }
        }

        // Draw glowing fill gradient under line
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(fillColor, Color.Transparent)
            )
        )

        // Draw main telemetry stroke line
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx())
        )

        // Draw last value point indicator dot
        if (dataPoints.isNotEmpty()) {
            val lastValue = dataPoints.last()
            val normalizedY = ((lastValue - minY) / rangeY).coerceIn(0f, 1f)
            val lastX = width
            val lastY = height - (normalizedY * height)

            drawCircle(
                color = lineColor,
                radius = 5.dp.toPx(),
                center = Offset(lastX, lastY)
            )
            drawCircle(
                color = Color.White,
                radius = 2.dp.toPx(),
                center = Offset(lastX, lastY)
            )
        }
    }
}

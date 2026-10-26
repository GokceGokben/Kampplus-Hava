package com.kampplus.hava.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CustomAnimatedSun(
    size: Dp = 64.dp,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sunRaysAnimation")
    val rayRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "rayRotation",
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(size.toPx() / 2f, size.toPx() / 2f)
            val radius = size.toPx() * 0.28f
            val rayStartRadius = radius * 1.3f
            val rayLength = radius * 0.5f

            // 1. İç çember sabit (dönmez)
            drawCircle(
                color = Color(0xFFFFB300),
                radius = radius,
                center = center,
            )

            // 2. Sadece dışındaki çentikler döner
            rotate(rayRotation, pivot = center) {
                val rayCount = 8
                for (i in 0 until rayCount) {
                    val angle = i * (360f / rayCount)
                    val rad = Math.toRadians(angle.toDouble())
                    val startX = center.x + (rayStartRadius * cos(rad)).toFloat()
                    val startY = center.y + (rayStartRadius * sin(rad)).toFloat()
                    val endX = center.x + ((rayStartRadius + rayLength) * cos(rad)).toFloat()
                    val endY = center.y + ((rayStartRadius + rayLength) * sin(rad)).toFloat()

                    drawLine(
                        color = Color(0xFFFF8F00),
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = size.toPx() * 0.06f,
                        cap = StrokeCap.Round,
                    )
                }
            }
        }
    }
}

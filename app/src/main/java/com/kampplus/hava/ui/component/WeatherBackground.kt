package com.kampplus.hava.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import top.yukonga.miuix.kmp.theme.MiuixTheme

/** WMO koduna ve gündüz/gece bilgisine göre gökyüzü tonu. */
fun weatherTint(weatherCode: Int?, isDay: Boolean): Color = when (weatherCode) {
    null -> Color(0xFF8A8F98)
    0 -> if (isDay) Color(0xFF3FA2FF) else Color(0xFF2B3A8F)
    1, 2, 3 -> if (isDay) Color(0xFF6FA3D1) else Color(0xFF3A4A6B)
    45, 48 -> Color(0xFF9AA5AD)
    51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82 -> if (isDay) Color(0xFF4A6FA5) else Color(0xFF2F4468)
    71, 73, 75, 77, 85, 86 -> Color(0xFF8FC7E8)
    95, 96, 99 -> Color(0xFF6A4FA3)
    else -> Color(0xFF8A8F98)
}

/**
 * Tahmin ekranının dinamik zemini: üstte hava durumu tonu, aşağı doğru tema yüzeyine solar.
 * Ton şeffaflıkla yüzeye karıştığı için metin kontrastı tema yüzeyine yakın kalır.
 */
@Composable
fun WeatherBackground(
    weatherCode: Int?,
    isDay: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val surface = MiuixTheme.colorScheme.surface
    val tintAlpha = if (isSystemInDarkTheme()) 0.5f else 0.28f
    val tint by animateColorAsState(
        targetValue = weatherTint(weatherCode, isDay).copy(alpha = tintAlpha),
        animationSpec = tween(durationMillis = 800),
        label = "weatherTint",
    )
    Box(
        modifier = modifier.background(
            Brush.verticalGradient(
                0f to tint,
                0.55f to surface,
                1f to surface,
            ),
        ),
    ) {
        content()
    }
}

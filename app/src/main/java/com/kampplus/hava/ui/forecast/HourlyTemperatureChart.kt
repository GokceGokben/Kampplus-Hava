package com.kampplus.hava.ui.forecast

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kampplus.hava.R
import com.kampplus.hava.model.HourlyForecast
import com.kampplus.hava.model.TemperatureUnit
import com.kampplus.hava.ui.theme.KampplusHavaTheme
import com.kampplus.hava.ui.theme.Spacing
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.theme.MiuixTheme

private val PointSpacing = 56.dp
private val ChartHeight = 190.dp
private val TopLabelSpace = 34.dp
private val BottomLabelSpace = 30.dp

/**
 * Saatlik sıcaklığı kaydırılabilir bir çizgi grafiği olarak çizer; çizgi soldan sağa açılarak belirir.
 * Yeni bağımlılık eklememek için Canvas ile çizilir.
 */
@Composable
fun HourlyTemperatureChart(
    hourly: List<HourlyForecast>,
    unit: TemperatureUnit,
    modifier: Modifier = Modifier,
) {
    if (hourly.size < 2) return

    val temps = hourly.map { unit.convert(it.temperature) }
    val minT = temps.min()
    val maxT = temps.max()
    val range = (maxT - minT).coerceAtLeast(1.0)

    val lineColor = MiuixTheme.colorScheme.primary
    val dotInner = MiuixTheme.colorScheme.surface
    val labelColor = MiuixTheme.colorScheme.onSurface
    val axisColor = MiuixTheme.colorScheme.onSurfaceVariantSummary
    val labelStyle = MiuixTheme.textStyles.footnote1.copy(color = labelColor)
    val axisStyle = MiuixTheme.textStyles.footnote1.copy(color = axisColor)
    val measurer = rememberTextMeasurer()

    val progress = remember { Animatable(0f) }
    LaunchedEffect(hourly, unit) {
        progress.snapTo(0f)
        progress.animateTo(1f, tween(durationMillis = 900, easing = FastOutSlowInEasing))
    }

    val description = stringResource(
        R.string.temperature_trend,
    ) + ": ${unit.round(hourly.minOf { it.temperature })}° – ${unit.round(hourly.maxOf { it.temperature })}°"

    Card(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .width(PointSpacing * hourly.size + Spacing.Large)
                .height(ChartHeight)
                .padding(horizontal = Spacing.Small)
                .semantics { contentDescription = description },
        ) {
            val spacing = PointSpacing.toPx()
            val top = TopLabelSpace.toPx()
            val bottom = size.height - BottomLabelSpace.toPx()
            val plotHeight = bottom - top
            val points = temps.mapIndexed { i, t ->
                Offset(
                    x = spacing / 2 + i * spacing,
                    y = bottom - ((t - minT) / range).toFloat() * plotHeight,
                )
            }

            // Noktalar arasında yumuşak (kübik) eğri
            val line = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    val prev = points[i - 1]
                    val cur = points[i]
                    val midX = (prev.x + cur.x) / 2
                    cubicTo(midX, prev.y, midX, cur.y, cur.x, cur.y)
                }
            }
            val fill = Path().apply {
                addPath(line)
                lineTo(points.last().x, size.height - BottomLabelSpace.toPx() + 8.dp.toPx())
                lineTo(points.first().x, size.height - BottomLabelSpace.toPx() + 8.dp.toPx())
                close()
            }

            clipRect(right = size.width * progress.value) {
                drawPath(
                    path = fill,
                    brush = Brush.verticalGradient(
                        colors = listOf(lineColor.copy(alpha = 0.28f), Color.Transparent),
                        startY = top,
                        endY = size.height,
                    ),
                )
                drawPath(
                    path = line,
                    color = lineColor,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
                )
                points.forEach { p ->
                    drawCircle(color = lineColor, radius = 5.dp.toPx(), center = p)
                    drawCircle(color = dotInner, radius = 2.5.dp.toPx(), center = p)
                }
            }

            // Etiketler çizgiyle birlikte belirir; saat etiketleri sabit alt şeritte
            points.forEachIndexed { i, p ->
                val reveal = (progress.value * points.size - i).coerceIn(0f, 1f)
                if (reveal <= 0f) return@forEachIndexed
                val tempLayout = measurer.measure("${unit.round(hourly[i].temperature)}°", labelStyle)
                drawText(
                    textLayoutResult = tempLayout,
                    topLeft = Offset(p.x - tempLayout.size.width / 2f, p.y - tempLayout.size.height - 10.dp.toPx()),
                    alpha = reveal,
                )
                val hourLayout = measurer.measure(hourly[i].time, axisStyle)
                drawText(
                    textLayoutResult = hourLayout,
                    topLeft = Offset(p.x - hourLayout.size.width / 2f, size.height - hourLayout.size.height - 4.dp.toPx()),
                    alpha = reveal,
                )
            }
        }
    }
}

private val previewHours = listOf(
    HourlyForecast("12:00", 18.0, 0), HourlyForecast("13:00", 20.0, 0),
    HourlyForecast("14:00", 22.0, 1), HourlyForecast("15:00", 23.0, 1),
    HourlyForecast("16:00", 21.0, 61), HourlyForecast("17:00", 19.0, 61),
    HourlyForecast("18:00", 17.0, 3), HourlyForecast("19:00", 15.0, 3),
)

@Preview(showBackground = true)
@Composable
private fun HourlyTemperatureChartPreview() {
    KampplusHavaTheme {
        HourlyTemperatureChart(hourly = previewHours, unit = TemperatureUnit.CELSIUS)
    }
}

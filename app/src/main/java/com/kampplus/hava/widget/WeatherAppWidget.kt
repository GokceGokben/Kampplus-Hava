package com.kampplus.hava.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.LocalContext
import com.kampplus.hava.R
import com.kampplus.hava.model.WeatherCondition

class WeatherAppWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                WeatherWidgetContent(
                    cityName = "Ankara",
                    temperature = "18°C",
                    condition = context.getString(WeatherCondition.fromWeatherCode(2).descriptionRes, 2),
                )
            }
        }
    }
}

@Composable
fun WeatherWidgetContent(
    cityName: String,
    temperature: String,
    condition: String,
) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.widgetBackground)
            .padding(16.dp),
        verticalAlignment = Alignment.Vertical.CenterVertically,
        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
    ) {
        Text(
            text = LocalContext.current.getString(R.string.app_name),
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = GlanceTheme.colors.primary,
            ),
        )
        Spacer(modifier = GlanceModifier.height(8.dp))
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
        ) {
            Column(horizontalAlignment = Alignment.Horizontal.CenterHorizontally) {
                Text(
                    text = cityName,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = GlanceTheme.colors.onSurface,
                    ),
                )
                Text(
                    text = condition,
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = GlanceTheme.colors.onSurfaceVariant,
                    ),
                )
            }
            Spacer(modifier = GlanceModifier.height(0.dp))
            Text(
                text = temperature,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = GlanceTheme.colors.primary,
                ),
            )
        }
    }
}

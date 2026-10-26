package com.kampplus.hava.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kampplus.hava.model.WeatherCondition

@Composable
fun AnimatedWeatherIcon(
    condition: WeatherCondition,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(id = condition.iconRes),
            contentDescription = condition.label(),
            modifier = Modifier.size(size),
        )
    }
}

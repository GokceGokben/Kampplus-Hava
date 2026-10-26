package com.kampplus.hava.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.kampplus.hava.ui.theme.KampplusHavaTheme
import com.kampplus.hava.ui.theme.Spacing
import top.yukonga.miuix.kmp.basic.Card

@Composable
fun ShimmerList(
    modifier: Modifier = Modifier,
    itemCount: Int = 6,
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerTranslation",
    )

    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f),
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value),
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = Spacing.Medium, vertical = Spacing.Small),
        verticalArrangement = Arrangement.spacedBy(Spacing.Medium),
    ) {
        items(itemCount) {
            ShimmerCardItem(brush = brush)
        }
    }
}

@Composable
fun ShimmerCardItem(brush: Brush) {
    // CityCard ile aynı iskelet: plaka rozeti + iki satır metin
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.Large),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(Spacing.MinTouchTarget)
                    .background(brush = brush, shape = RoundedCornerShape(Spacing.Medium)),
            )
            Spacer(modifier = Modifier.width(Spacing.Large))
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(Spacing.Large)
                        .background(brush = brush, shape = RoundedCornerShape(Spacing.ExtraSmall)),
                )
                Spacer(modifier = Modifier.height(Spacing.Small))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(Spacing.Medium)
                        .background(brush = brush, shape = RoundedCornerShape(Spacing.ExtraSmall)),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShimmerListPreview() {
    KampplusHavaTheme {
        ShimmerList()
    }
}

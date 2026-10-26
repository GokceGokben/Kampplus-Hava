package com.kampplus.hava.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Şehir kartı ile tahmin ekranı arasında "kapsayıcı dönüşümü" sağlar. İki kapsam da yoksa (önizleme, test) modifier'a dokunmaz.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.sharedCityBounds(
    cityName: String,
    sharedTransitionScope: SharedTransitionScope?,
    animatedVisibilityScope: AnimatedVisibilityScope?,
): Modifier {
    if (sharedTransitionScope == null || animatedVisibilityScope == null) return this
    return with(sharedTransitionScope) {
        this@sharedCityBounds.sharedBounds(
            sharedContentState = rememberSharedContentState(key = "city-$cityName"),
            animatedVisibilityScope = animatedVisibilityScope,
            enter = fadeIn(),
            exit = fadeOut(),
        )
    }
}

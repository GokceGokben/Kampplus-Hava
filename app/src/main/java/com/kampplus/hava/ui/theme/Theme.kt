package com.kampplus.hava.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme

/**
 * Uygulama teması: Miuix (HyperOS) renk ve yazı tipi token'ları.
 * Tek vurgu rengi Miuix paletindeki `primary`; geri kalan her şey nötr.
 */
@Composable
fun KampplusHavaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MiuixTheme(
        colors = if (darkTheme) darkColorScheme() else lightColorScheme(),
        content = content,
    )
}

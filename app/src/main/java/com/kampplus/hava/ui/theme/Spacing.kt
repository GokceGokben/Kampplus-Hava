package com.kampplus.hava.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Miuix bileşen varsayılanlarından türetilen boşluk ritmi. Ekranlarda serbest dp değeri yerine bunlar kullanılır.
 */
object Spacing {
    /** Satır içi metinler arası en küçük boşluk (Small'ın yarısı). */
    val ExtraSmall = 4.dp

    /** SmallTitleDefaults.InsideMargin dikey boşluğu. */
    val Small = 8.dp

    /** Miuix örneklerindeki kart grupları arası / sayfa kenar boşluğu. */
    val Medium = 12.dp

    /** BasicComponentDefaults.InsideMargin ve TextFieldDefaults.InsideMargin. */
    val Large = 16.dp

    /** Bölümler arası boşluk (Medium'un iki katı). */
    val ExtraLarge = 24.dp

    /** Erişilebilir en küçük dokunma alanı. */
    val MinTouchTarget = 48.dp
}

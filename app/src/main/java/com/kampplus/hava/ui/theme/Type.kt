package com.kampplus.hava.ui.theme

import androidx.compose.ui.text.TextStyle

/**
 * Sabit genişlikli (tabular) rakamlar: sıcaklık ve saat sütunları değer değiştikçe kaymaz.
 */
fun TextStyle.tabularNumerals(): TextStyle = copy(fontFeatureSettings = "tnum")

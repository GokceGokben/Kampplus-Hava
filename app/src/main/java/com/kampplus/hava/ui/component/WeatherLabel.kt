package com.kampplus.hava.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.kampplus.hava.model.WeatherCondition

/** Yerelleştirilmiş durum metni. Bilinmeyen kodda WMO kodu metne eklenir; diğerlerinde argüman yok sayılır. */
@Composable
fun WeatherCondition.label(): String = stringResource(descriptionRes, code)

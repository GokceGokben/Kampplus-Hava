package com.kampplus.hava.ui.forecast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.kampplus.hava.R
import com.kampplus.hava.model.CurrentWeather
import com.kampplus.hava.model.DailyForecast
import com.kampplus.hava.model.Forecast
import com.kampplus.hava.model.HourlyForecast
import com.kampplus.hava.model.TemperatureUnit
import com.kampplus.hava.model.WeatherCondition
import com.kampplus.hava.ui.LoadState
import com.kampplus.hava.ui.component.AnimatedWeatherIcon
import com.kampplus.hava.ui.component.EmptyView
import com.kampplus.hava.ui.component.ErrorView
import com.kampplus.hava.ui.component.WeatherBackground
import com.kampplus.hava.ui.component.label
import com.kampplus.hava.ui.sharedCityBounds
import com.kampplus.hava.ui.theme.KampplusHavaTheme
import com.kampplus.hava.ui.theme.Spacing
import com.kampplus.hava.ui.theme.tabularNumerals
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.PullToRefresh
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.theme.MiuixTheme
import kotlin.math.roundToInt

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ForecastScreen(
    cityName: String,
    loadState: LoadState<Forecast>,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    isRefreshing: Boolean = false,
    unit: TemperatureUnit = TemperatureUnit.CELSIUS,
    isFavorite: Boolean = false,
    onRefresh: () -> Unit = {},
    onToggleUnit: () -> Unit = {},
    onToggleFavorite: () -> Unit = {},
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
) {
    val current = (loadState as? LoadState.Success)?.data?.current

    WeatherBackground(
        weatherCode = current?.weatherCode,
        isDay = current?.isDay ?: true,
        modifier = modifier
            .fillMaxSize()
            .sharedCityBounds(cityName, sharedTransitionScope, animatedVisibilityScope),
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                SmallTopAppBar(
                    title = cityName,
                    color = Color.Transparent,
                    navigationIcon = {
                        IconButton(
                            onClick = onBackClick,
                            minHeight = Spacing.MinTouchTarget,
                            minWidth = Spacing.MinTouchTarget,
                        ) {
                            // Miuix çekirdek ikon setinde geri oku yok; Material ikonu Miuix Icon ile çizilir
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back),
                                tint = MiuixTheme.colorScheme.onSurface,
                            )
                        }
                    },
                    actions = {
                        val toggleUnitDescription = stringResource(R.string.toggle_unit)
                        TextButton(
                            text = unit.symbol,
                            onClick = onToggleUnit,
                            minHeight = Spacing.MinTouchTarget,
                            minWidth = Spacing.MinTouchTarget,
                            modifier = Modifier.semantics { contentDescription = toggleUnitDescription },
                        )
                        IconButton(
                            onClick = onToggleFavorite,
                            minHeight = Spacing.MinTouchTarget,
                            minWidth = Spacing.MinTouchTarget,
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                contentDescription = stringResource(
                                    if (isFavorite) R.string.remove_favorite else R.string.add_favorite,
                                ),
                                tint = if (isFavorite) {
                                    MiuixTheme.colorScheme.primary
                                } else {
                                    MiuixTheme.colorScheme.onSurfaceVariantSummary
                                },
                            )
                        }
                    },
                )
            },
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                when (loadState) {
                    is LoadState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(color = MiuixTheme.colorScheme.primary)
                        }
                    }

                    is LoadState.Empty -> {
                        EmptyView(message = stringResource(R.string.no_weather_data))
                    }

                    is LoadState.Error -> {
                        ErrorView(
                            message = loadState.message,
                            isNetwork = loadState.isNetwork,
                            fallbackMessage = stringResource(R.string.error_loading_weather),
                            onRetry = onRetry,
                        )
                    }

                    is LoadState.Success -> {
                        PullToRefresh(
                            isRefreshing = isRefreshing,
                            onRefresh = onRefresh,
                            refreshTexts = listOf(
                                stringResource(R.string.pull_to_refresh),
                                stringResource(R.string.release_to_refresh),
                                stringResource(R.string.refreshing),
                                stringResource(R.string.refreshed),
                            ),
                        ) {
                            ForecastContent(forecast = loadState.data, unit = unit)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ForecastContent(
    forecast: Forecast,
    unit: TemperatureUnit = TemperatureUnit.CELSIUS,
) {
    val condition = WeatherCondition.fromWeatherCode(forecast.current.weatherCode)
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(500)) + slideInVertically(
            animationSpec = tween(500),
            initialOffsetY = { 40 },
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.Medium, vertical = Spacing.Small),
        ) {
            // Anlık Hava Durumu: ekranın baskın öğesi sıcaklık
            CurrentWeatherHeader(current = forecast.current, condition = condition, unit = unit)

            // Ayrıntı kartları (rüzgar, nem, UV, basınç, görüş, gün doğumu/batımı)
            SectionTitle(text = stringResource(R.string.details_title))
            WeatherDetailsGrid(
                current = forecast.current,
                today = forecast.daily.firstOrNull(),
            )

            // Saatlik Tahmin Bölümü
            if (forecast.hourly.isNotEmpty()) {
                val next24 = forecast.hourly.take(24)
                SectionTitle(text = stringResource(R.string.hourly_forecast))
                Card(modifier = Modifier.fillMaxWidth()) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = Spacing.Small),
                    ) {
                        itemsIndexed(next24) { index, hourly ->
                            HourlyForecastCard(
                                hourly = hourly,
                                unit = unit,
                                isNow = index == 0,
                            )
                        }
                    }
                }

                if (next24.size >= 2) {
                    SectionTitle(text = stringResource(R.string.temperature_trend))
                    HourlyTemperatureChart(hourly = next24, unit = unit)
                }
            }

            // 7 Günlük Tahmin Bölümü
            if (forecast.daily.isNotEmpty()) {
                SectionTitle(text = stringResource(R.string.daily_forecast))
                Card(modifier = Modifier.fillMaxWidth()) {
                    forecast.daily.forEachIndexed { index, daily ->
                        if (index > 0) {
                            HorizontalDivider(modifier = Modifier.padding(horizontal = Spacing.Large))
                        }
                        DailyForecastItem(daily = daily, index = index, unit = unit)
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.ExtraLarge))
        }
    }
}

@Composable
fun CurrentWeatherHeader(
    current: CurrentWeather,
    condition: WeatherCondition,
    unit: TemperatureUnit = TemperatureUnit.CELSIUS,
) {
    val title1 = MiuixTheme.textStyles.title1

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Spacing.ExtraLarge, bottom = Spacing.Small),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.ExtraSmall),
    ) {
        AnimatedWeatherIcon(
            condition = condition,
            size = Spacing.MinTouchTarget * 2,
        )
        // Miuix'in en büyük başlığı (title1) iki katına çıkarılır; vurgu rengi sadece bu değerde
        Text(
            text = "${unit.round(current.temperature)}°",
            style = title1.copy(fontSize = title1.fontSize * 2, lineHeight = title1.fontSize * 2).tabularNumerals(),
            fontWeight = FontWeight.Light,
            color = MiuixTheme.colorScheme.primary,
        )
        Text(
            text = condition.label(),
            style = MiuixTheme.textStyles.title4,
            color = MiuixTheme.colorScheme.onSurface,
        )
        Text(
            text = stringResource(
                R.string.feels_like,
                "${unit.round(current.apparentTemperature)}${unit.symbol}",
            ),
            style = MiuixTheme.textStyles.body2.tabularNumerals(),
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    SmallTitle(
        text = text,
        modifier = Modifier.padding(top = Spacing.Medium),
        textColor = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        insideMargin = PaddingValues(horizontal = Spacing.Large, vertical = Spacing.Small),
    )
}

private data class DetailTileData(
    val label: String,
    val value: String,
    val note: String? = null,
)

/** İki sütunlu ayrıntı kartları; tek sayıda öğe kalırsa son satır boş bir yer tutucuyla hizalanır. */
@Composable
fun WeatherDetailsGrid(
    current: CurrentWeather,
    today: DailyForecast?,
    modifier: Modifier = Modifier,
) {
    val tiles = buildList {
        add(
            DetailTileData(
                label = stringResource(R.string.wind),
                value = stringResource(R.string.wind_value, current.windSpeed.roundToInt().toString()),
            ),
        )
        add(
            DetailTileData(
                label = stringResource(R.string.humidity),
                value = stringResource(R.string.humidity_value, current.relativeHumidity),
            ),
        )
        add(
            DetailTileData(
                label = stringResource(R.string.precipitation),
                value = stringResource(R.string.precipitation_value, current.precipitation.oneDecimal()),
            ),
        )
        add(
            DetailTileData(
                label = stringResource(R.string.uv_index),
                value = current.uvIndex.roundToInt().toString(),
                note = stringResource(uvLevelRes(current.uvIndex)),
            ),
        )
        if (current.pressure > 0) {
            add(
                DetailTileData(
                    label = stringResource(R.string.pressure),
                    value = stringResource(R.string.pressure_value, current.pressure.roundToInt()),
                ),
            )
        }
        if (current.visibilityKm > 0) {
            val visibility = if (current.visibilityKm >= 10) {
                current.visibilityKm.roundToInt().toString()
            } else {
                current.visibilityKm.oneDecimal()
            }
            add(
                DetailTileData(
                    label = stringResource(R.string.visibility),
                    value = stringResource(R.string.visibility_value, visibility),
                ),
            )
        }
        if (today != null && today.sunrise.isNotBlank()) {
            add(DetailTileData(label = stringResource(R.string.sunrise), value = today.sunrise))
        }
        if (today != null && today.sunset.isNotBlank()) {
            add(DetailTileData(label = stringResource(R.string.sunset), value = today.sunset))
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.Medium),
    ) {
        tiles.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.Medium),
            ) {
                row.forEach { tile ->
                    WeatherDetailItem(
                        label = tile.label,
                        value = tile.value,
                        note = tile.note,
                        modifier = Modifier.weight(1f),
                    )
                }
                if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun WeatherDetailItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    note: String? = null,
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.Large),
            verticalArrangement = Arrangement.spacedBy(Spacing.ExtraSmall),
        ) {
            Text(
                text = label,
                style = MiuixTheme.textStyles.footnote1,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            )
            Text(
                text = value,
                style = MiuixTheme.textStyles.title3.tabularNumerals(),
                color = MiuixTheme.colorScheme.onSurface,
            )
            if (note != null) {
                Text(
                    text = note,
                    style = MiuixTheme.textStyles.footnote1,
                    color = MiuixTheme.colorScheme.onSurfaceSecondary,
                )
            }
        }
    }
}

@Composable
fun HourlyForecastCard(
    hourly: HourlyForecast,
    unit: TemperatureUnit = TemperatureUnit.CELSIUS,
    isNow: Boolean = false,
) {
    val cond = WeatherCondition.fromWeatherCode(hourly.weatherCode)
    Column(
        modifier = Modifier.padding(horizontal = Spacing.Medium, vertical = Spacing.Large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Small),
    ) {
        Text(
            text = if (isNow) stringResource(R.string.now) else hourly.time,
            style = MiuixTheme.textStyles.footnote1.tabularNumerals(),
            fontWeight = if (isNow) FontWeight.Bold else null,
            color = if (isNow) MiuixTheme.colorScheme.onSurface else MiuixTheme.colorScheme.onSurfaceVariantSummary,
        )
        AnimatedWeatherIcon(
            condition = cond,
            size = Spacing.ExtraLarge + Spacing.Small,
        )
        Text(
            text = "${unit.round(hourly.temperature)}°",
            style = MiuixTheme.textStyles.body1.tabularNumerals(),
            fontWeight = FontWeight.Medium,
            color = MiuixTheme.colorScheme.onSurface,
        )
    }
}

@Composable
fun DailyForecastItem(
    daily: DailyForecast,
    index: Int,
    unit: TemperatureUnit = TemperatureUnit.CELSIUS,
) {
    val cond = WeatherCondition.fromWeatherCode(daily.weatherCode)
    val locale = TurkishLocale
    val parts = parseDayParts(daily.date, locale)
    val title = when (index) {
        0 -> stringResource(R.string.today)
        1 -> stringResource(R.string.tomorrow)
        else -> parts?.weekday ?: daily.date
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.Large, vertical = Spacing.Medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Medium),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MiuixTheme.textStyles.body1,
                fontWeight = if (index == 0) FontWeight.Bold else null,
                color = MiuixTheme.colorScheme.onSurface,
            )
            if (parts != null) {
                Text(
                    text = parts.shortDate,
                    style = MiuixTheme.textStyles.footnote1,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )
            }
        }
        Row(
            modifier = Modifier.weight(1.3f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Small),
        ) {
            AnimatedWeatherIcon(
                condition = cond,
                size = Spacing.ExtraLarge + Spacing.Small,
            )
            Text(
                text = cond.label(),
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.Small)) {
            Text(
                text = "${unit.round(daily.maxTemp)}°",
                style = MiuixTheme.textStyles.body1.tabularNumerals(),
                fontWeight = FontWeight.Medium,
                color = MiuixTheme.colorScheme.onSurface,
            )
            Text(
                text = "${unit.round(daily.minTemp)}°",
                style = MiuixTheme.textStyles.body1.tabularNumerals(),
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            )
        }
    }
}

private val previewForecast = Forecast(
    cityName = "Ankara",
    current = CurrentWeather(
        temperature = 22.4,
        apparentTemperature = 21.0,
        relativeHumidity = 45,
        isDay = true,
        precipitation = 0.0,
        weatherCode = 0,
        windSpeed = 12.5,
        pressure = 1013.0,
        visibilityKm = 24.1,
        uvIndex = 5.2,
    ),
    hourly = listOf(
        HourlyForecast("12:00", 22.0, 0),
        HourlyForecast("13:00", 23.0, 1),
        HourlyForecast("14:00", 22.5, 61),
        HourlyForecast("15:00", 21.0, 3),
        HourlyForecast("16:00", 19.5, 95),
    ),
    daily = listOf(
        DailyForecast("2025-02-28", 23.0, 10.0, 0, sunrise = "06:41", sunset = "18:12"),
        DailyForecast("2025-03-01", 19.0, 8.0, 61),
        DailyForecast("2025-03-02", 12.0, 3.0, 71),
    ),
)

@Preview(showBackground = true)
@Composable
fun ForecastScreenPreview() {
    KampplusHavaTheme {
        ForecastScreen(
            cityName = "Ankara",
            loadState = LoadState.Success(previewForecast),
            onBackClick = {},
            onRetry = {},
        )
    }
}

@Preview(showBackground = true, name = "Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ForecastScreenDarkPreview() {
    KampplusHavaTheme(darkTheme = true) {
        ForecastScreen(
            cityName = "Ankara",
            loadState = LoadState.Success(previewForecast),
            onBackClick = {},
            onRetry = {},
            isFavorite = true,
            unit = TemperatureUnit.FAHRENHEIT,
        )
    }
}

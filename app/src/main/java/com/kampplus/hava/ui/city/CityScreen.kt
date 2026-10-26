package com.kampplus.hava.ui.city

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.kampplus.hava.R
import com.kampplus.hava.model.City
import com.kampplus.hava.model.sampleCities
import com.kampplus.hava.model.suggestionSentence
import com.kampplus.hava.ui.LoadState
import com.kampplus.hava.ui.component.EmptyView
import com.kampplus.hava.ui.component.ErrorView
import com.kampplus.hava.ui.component.ShimmerList
import com.kampplus.hava.ui.sharedCityBounds
import com.kampplus.hava.ui.theme.KampplusHavaTheme
import com.kampplus.hava.ui.theme.Spacing
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.basic.ArrowRight
import top.yukonga.miuix.kmp.icon.basic.Search
import top.yukonga.miuix.kmp.icon.basic.SearchCleanup
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.miuixCapsuleShape

/** Konum önerisi gösterilemediğinde kullanıcıya nedenini anlatan kart türleri. Karta dokunmak izin ister veya yeniden dener. */
enum class LocationPrompt(@StringRes val textRes: Int, @StringRes val subtitleRes: Int) {
    PermissionNeeded(R.string.location_prompt_text, R.string.location_prompt_subtitle),
    OutsideTurkey(R.string.location_outside_text, R.string.location_outside_subtitle),
    Unavailable(R.string.location_unavailable_text, R.string.location_unavailable_subtitle),
}

@Composable
fun CityScreen(
    query: String,
    loadState: LoadState<List<City>>,
    suggestedCity: City?,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onRetry: () -> Unit,
    onCityClick: (City) -> Unit,
    modifier: Modifier = Modifier,
    locationPrompt: LocationPrompt? = null,
    onRequestLocation: () -> Unit = {},
    favorites: List<City> = emptyList(),
    onToggleFavorite: (City) -> Unit = {},
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
) {
    val favoriteNames = remember(favorites) { favorites.map { it.name }.toSet() }
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(500)) + slideInVertically(
            animationSpec = tween(500),
            initialOffsetY = { 30 },
        ),
        modifier = modifier.fillMaxSize(),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Arama Kutusu
            CitySearchBar(
                query = query,
                onQueryChange = onQueryChange,
                onClearClick = onClearQuery,
                modifier = Modifier.padding(horizontal = Spacing.Medium, vertical = Spacing.Small),
            )

            // Konum tabanlı öneri (arama boşken animasyonlu görünür). İzin yoksa izin isteyen kart gösterilir.
            AnimatedVisibility(
                visible = query.isBlank() && (suggestedCity != null || locationPrompt != null),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                Box(modifier = Modifier.padding(horizontal = Spacing.Medium, vertical = Spacing.ExtraSmall)) {
                    if (suggestedCity != null) {
                        LocationSuggestionCard(
                            city = suggestedCity,
                            onClick = { onCityClick(suggestedCity) },
                        )
                    } else {
                        locationPrompt?.let { prompt ->
                            LocationCard(
                                text = stringResource(prompt.textRes),
                                subtitle = stringResource(prompt.subtitleRes),
                                onClick = onRequestLocation,
                            )
                        }
                    }
                }
            }

        // Dört Durum Yönetimi (Loading, Success, Empty, Error)
        when (loadState) {
            is LoadState.Loading -> {
                ShimmerList()
            }

            is LoadState.Empty -> {
                EmptyView(
                    message = if (query.isBlank()) {
                        stringResource(R.string.no_cities_available)
                    } else {
                        stringResource(R.string.city_not_found)
                    },
                )
            }

            is LoadState.Error -> {
                ErrorView(
                    message = loadState.message,
                    isNetwork = loadState.isNetwork,
                    fallbackMessage = stringResource(R.string.error_loading_cities),
                    onRetry = onRetry,
                )
            }

            is LoadState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = Spacing.Medium, vertical = Spacing.Small),
                    verticalArrangement = Arrangement.spacedBy(Spacing.Medium),
                ) {
                    // Favoriler yalnızca arama boşken, listenin başında gösterilir
                    if (query.isBlank() && favorites.isNotEmpty()) {
                        item(key = "header_favorites") {
                            ListSectionTitle(text = stringResource(R.string.favorites_title))
                        }
                        items(items = favorites, key = { "fav_${it.name}" }) { city ->
                            CityCard(
                                city = city,
                                onClick = { onCityClick(city) },
                                isFavorite = true,
                                onFavoriteClick = { onToggleFavorite(city) },
                            )
                        }
                        item(key = "header_all") {
                            ListSectionTitle(text = stringResource(R.string.all_cities_title))
                        }
                    }
                    items(
                        items = loadState.data,
                        key = { "city_${it.id}" },
                    ) { city ->
                        CityCard(
                            city = city,
                            onClick = { onCityClick(city) },
                            isFavorite = city.name in favoriteNames,
                            onFavoriteClick = { onToggleFavorite(city) },
                            modifier = Modifier.sharedCityBounds(
                                cityName = city.name,
                                sharedTransitionScope = sharedTransitionScope,
                                animatedVisibilityScope = animatedVisibilityScope,
                            ),
                        )
                    }
                }
            }
        }
      }
    }
}

@Composable
fun LocationSuggestionCard(
    city: City,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LocationCard(
        text = city.suggestionSentence,
        subtitle = stringResource(R.string.location_suggestion_subtitle),
        onClick = onClick,
        modifier = modifier,
    )
}

@Composable
private fun LocationCard(
    text: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Listedeki şehir kartlarından ayrışsın diye vurgu renginin açık tonu (tertiaryContainer) ve konum ikonu.
    // Bu zeminde ikincil metin onSurfaceSecondary'dir: onSurfaceVariantSummary koyu temada AA'nın altında kalıyor.
    val colors = MiuixTheme.colorScheme
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.defaultColors(
            color = colors.tertiaryContainer,
            contentColor = colors.onSurface,
        ),
        showIndication = true,
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.Large),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Large),
        ) {
            Surface(
                shape = miuixCapsuleShape(),
                color = colors.primary,
                modifier = Modifier.size(Spacing.MinTouchTarget),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.MyLocation,
                        contentDescription = stringResource(R.string.location_icon),
                        tint = colors.onPrimary,
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.ExtraSmall),
            ) {
                Text(
                    text = stringResource(R.string.location_suggestion_title),
                    style = MiuixTheme.textStyles.footnote1,
                    fontWeight = FontWeight.Medium,
                    color = colors.onSurfaceSecondary,
                )
                Text(
                    text = text,
                    style = MiuixTheme.textStyles.main,
                    fontWeight = FontWeight.Medium,
                    color = colors.onSurface,
                )
                Text(
                    text = subtitle,
                    style = MiuixTheme.textStyles.footnote1,
                    color = colors.onSurfaceSecondary,
                )
            }

            Icon(
                imageVector = MiuixIcons.Basic.ArrowRight,
                contentDescription = stringResource(R.string.open_weather),
                tint = colors.onSurfaceSecondary,
            )
        }
    }
}

@Composable
private fun ListSectionTitle(text: String) {
    SmallTitle(
        text = text,
        textColor = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        insideMargin = PaddingValues(horizontal = Spacing.Small, vertical = Spacing.Small),
    )
}

/**
 * Miuix SearchBar InputField token'larıyla (kapsül şekil, surfaceContainerHigh zemin) kurulan arama kutusu.
 * Miuix'in kendi alanları odak/temizle ikonunda animasyon içerdiği için (yeni animasyon eklememek adına)
 * BasicTextField kullanılır. Yer tutucu rengi WCAG AA için onSurfaceVariantSummary'dir.
 */
@Composable
fun CitySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MiuixTheme.colorScheme
    val textStyle = MiuixTheme.textStyles.main.copy(color = colors.onSurface)

    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        textStyle = textStyle,
        cursorBrush = SolidColor(colors.primary),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = Spacing.MinTouchTarget)
                    .background(color = colors.surfaceContainerHigh, shape = miuixCapsuleShape())
                    .padding(start = Spacing.Large),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.Small),
            ) {
                Icon(
                    imageVector = MiuixIcons.Basic.Search,
                    contentDescription = stringResource(R.string.search_content_description),
                    tint = colors.onSurfaceVariantSummary,
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = Spacing.Large),
                ) {
                    if (query.isEmpty()) {
                        Text(
                            text = stringResource(R.string.search_hint),
                            style = textStyle,
                            color = colors.onSurfaceVariantSummary,
                        )
                    }
                    innerTextField()
                }
                if (query.isNotEmpty()) {
                    IconButton(
                        onClick = onClearClick,
                        minHeight = Spacing.MinTouchTarget,
                        minWidth = Spacing.MinTouchTarget,
                    ) {
                        Icon(
                            imageVector = MiuixIcons.Basic.SearchCleanup,
                            contentDescription = stringResource(R.string.clear_search),
                            tint = colors.onSurfaceContainerHighest,
                        )
                    }
                } else {
                    Box(modifier = Modifier.padding(end = Spacing.Small))
                }
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
fun CityScreenSuccessPreview() {
    KampplusHavaTheme {
        Surface {
            CityScreen(
                query = "Ankara",
                loadState = LoadState.Success(sampleCities),
                suggestedCity = sampleCities.first(),
                onQueryChange = {},
                onClearQuery = {},
                onRetry = {},
                onCityClick = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CityScreenLoadingPreview() {
    KampplusHavaTheme {
        Surface {
            CityScreen(
                query = "",
                loadState = LoadState.Loading,
                suggestedCity = sampleCities.first(),
                onQueryChange = {},
                onClearQuery = {},
                onRetry = {},
                onCityClick = {},
            )
        }
    }
}

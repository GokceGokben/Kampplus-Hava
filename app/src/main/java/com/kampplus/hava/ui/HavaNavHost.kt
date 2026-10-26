package com.kampplus.hava.ui

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kampplus.hava.R
import com.kampplus.hava.data.LocationProvider
import com.kampplus.hava.data.ServiceLocator
import com.kampplus.hava.model.City
import com.kampplus.hava.model.turkeyProvinces
import com.kampplus.hava.ui.city.CityScreen
import com.kampplus.hava.ui.city.LocationPrompt
import com.kampplus.hava.ui.city.CityViewModel
import com.kampplus.hava.ui.forecast.ForecastScreen
import com.kampplus.hava.ui.forecast.ForecastViewModel
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object HavaRoutes {
    const val CITY_LIST = "city_list"
    const val FORECAST = "forecast/{cityName}/{lat}/{lng}"

    fun forecastRoute(cityName: String, latitude: Double, longitude: Double): String {
        val encodedCity = URLEncoder.encode(cityName, StandardCharsets.UTF_8.toString())
        return "forecast/$encodedCity/$latitude/$longitude"
    }
}

private const val NAV_FADE_MILLIS = 300

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HavaNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val context = LocalContext.current.applicationContext
    val favoritesRepository = remember { ServiceLocator.favorites(context) }
    val settingsRepository = remember { ServiceLocator.settings(context) }

    // Kart -> tahmin ekranı geçişi paylaşılan sınırlarla (kapsayıcı dönüşümü) yapılır; geri hareketi bu geçişi önizler.
    SharedTransitionLayout(modifier = modifier) {
        val sharedTransitionScope = this

        NavHost(
            navController = navController,
            startDestination = HavaRoutes.CITY_LIST,
            enterTransition = { fadeIn(tween(NAV_FADE_MILLIS)) },
            exitTransition = { fadeOut(tween(NAV_FADE_MILLIS)) },
            popEnterTransition = { fadeIn(tween(NAV_FADE_MILLIS)) },
            popExitTransition = { fadeOut(tween(NAV_FADE_MILLIS)) },
        ) {
            composable(HavaRoutes.CITY_LIST) {
                val animatedScope = this
                val cityViewModel: CityViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T =
                            CityViewModel(favoritesRepository = favoritesRepository) as T
                    },
                )
                val query by cityViewModel.query.collectAsStateWithLifecycle()
                val loadState by cityViewModel.loadState.collectAsStateWithLifecycle()
                val suggestedCity by cityViewModel.suggestedCity.collectAsStateWithLifecycle()
                val favorites by cityViewModel.favorites.collectAsStateWithLifecycle()

                // Konum izni: verilirse en yakın il önerilir, verilmezse izin isteyen kart gösterilir
                var hasLocationPermission by remember { mutableStateOf(LocationProvider.hasPermission(context)) }
                var locationPrompt by remember {
                    mutableStateOf(if (hasLocationPermission) null else LocationPrompt.PermissionNeeded)
                }
                var locationRetry by remember { mutableIntStateOf(0) }
                val permissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission(),
                ) { granted -> hasLocationPermission = granted }
                LaunchedEffect(hasLocationPermission, locationRetry) {
                    if (!hasLocationPermission) {
                        locationPrompt = LocationPrompt.PermissionNeeded
                        return@LaunchedEffect
                    }
                    val location = LocationProvider.currentLocation(context)
                    locationPrompt = when {
                        location == null -> LocationPrompt.Unavailable
                        cityViewModel.suggestFromLocation(location.latitude, location.longitude) -> null
                        else -> LocationPrompt.OutsideTurkey
                    }
                }

                Scaffold(
                    topBar = {
                        SmallTopAppBar(title = stringResource(R.string.city_list_title))
                    },
                ) { innerPadding ->
                    CityScreen(
                        query = query,
                        loadState = loadState,
                        suggestedCity = suggestedCity,
                        onQueryChange = cityViewModel::onQueryChange,
                        onClearQuery = cityViewModel::clearQuery,
                        onRetry = cityViewModel::retry,
                        onCityClick = { city ->
                            Log.d("HavaNavHost", "City clicked: ${city.name} (${city.latitude}, ${city.longitude})")
                            navController.navigate(
                                HavaRoutes.forecastRoute(
                                    cityName = city.name,
                                    latitude = city.latitude,
                                    longitude = city.longitude,
                                ),
                            )
                        },
                        locationPrompt = locationPrompt,
                        onRequestLocation = {
                            if (hasLocationPermission) {
                                locationRetry++
                            } else {
                                permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                            }
                        },
                        favorites = favorites,
                        onToggleFavorite = cityViewModel::toggleFavorite,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedScope,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }

            composable(
                route = HavaRoutes.FORECAST,
                arguments = listOf(
                    navArgument("cityName") { type = NavType.StringType },
                    navArgument("lat") { type = NavType.FloatType },
                    navArgument("lng") { type = NavType.FloatType },
                ),
            ) { backStackEntry ->
                val animatedScope = this
                val rawCityName = backStackEntry.arguments?.getString("cityName") ?: ""
                val cityName = URLDecoder.decode(rawCityName, StandardCharsets.UTF_8.toString())
                val lat = backStackEntry.arguments?.getFloat("lat")?.toDouble() ?: 0.0
                val lng = backStackEntry.arguments?.getFloat("lng")?.toDouble() ?: 0.0

                val forecastViewModel: ForecastViewModel = viewModel()
                val loadState by forecastViewModel.loadState.collectAsStateWithLifecycle()
                val isRefreshing by forecastViewModel.isRefreshing.collectAsStateWithLifecycle()
                val unit by settingsRepository.temperatureUnit.collectAsStateWithLifecycle()
                val favorites by favoritesRepository.favorites.collectAsStateWithLifecycle()

                LaunchedEffect(cityName, lat, lng) {
                    forecastViewModel.loadForecast(
                        cityName = cityName,
                        latitude = lat,
                        longitude = lng,
                    )
                }

                ForecastScreen(
                    cityName = cityName,
                    loadState = loadState,
                    isRefreshing = isRefreshing,
                    unit = unit,
                    isFavorite = favorites.any { it.name == cityName },
                    onBackClick = { navController.popBackStack() },
                    onRetry = forecastViewModel::retry,
                    onRefresh = forecastViewModel::refresh,
                    onToggleUnit = settingsRepository::toggleTemperatureUnit,
                    onToggleFavorite = {
                        favoritesRepository.toggle(cityFor(cityName, lat, lng))
                    },
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedScope,
                )
            }
        }
    }
}

/** Favori kaydı için rota argümanlarından şehir nesnesi kurar; bilinen illerde plaka/ülke bilgisi korunur. */
private fun cityFor(name: String, latitude: Double, longitude: Double): City =
    turkeyProvinces.firstOrNull { it.name == name }?.copy(latitude = latitude, longitude = longitude)
        ?: City(id = name.hashCode().toLong(), name = name, latitude = latitude, longitude = longitude)

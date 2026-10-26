package com.kampplus.hava.ui.forecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kampplus.hava.data.WeatherRepository
import com.kampplus.hava.model.Forecast
import com.kampplus.hava.ui.LoadState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class ForecastViewModel(
    private val weatherRepository: WeatherRepository = WeatherRepository(),
) : ViewModel() {

    private val _loadState = MutableStateFlow<LoadState<Forecast>>(LoadState.Loading)
    val loadState: StateFlow<LoadState<Forecast>> = _loadState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var currentLat: Double = 0.0
    private var currentLng: Double = 0.0
    private var currentCityName: String = ""

    fun loadForecast(cityName: String, latitude: Double, longitude: Double) {
        currentCityName = cityName
        currentLat = latitude
        currentLng = longitude
        fetchForecast()
    }

    fun retry() {
        fetchForecast()
    }

    /** Çek-bırak yenileme: mevcut içerik ekranda kalır, hata olursa eski veri korunur. */
    fun refresh() {
        if (_loadState.value !is LoadState.Success) {
            fetchForecast()
            return
        }
        viewModelScope.launch {
            _isRefreshing.value = true
            weatherRepository.getForecast(
                latitude = currentLat,
                longitude = currentLng,
                cityName = currentCityName,
            ).onSuccess { forecast ->
                _loadState.value = LoadState.Success(forecast)
            }
            _isRefreshing.value = false
        }
    }

    private fun fetchForecast() {
        viewModelScope.launch {
            _loadState.value = LoadState.Loading
            val result = weatherRepository.getForecast(
                latitude = currentLat,
                longitude = currentLng,
                cityName = currentCityName,
            )
            result.fold(
                onSuccess = { forecast ->
                    _loadState.value = LoadState.Success(forecast)
                },
                onFailure = { error ->
                    _loadState.value = LoadState.Error(
                        message = error.localizedMessage.orEmpty(),
                        isNetwork = error is IOException,
                    )
                },
            )
        }
    }
}

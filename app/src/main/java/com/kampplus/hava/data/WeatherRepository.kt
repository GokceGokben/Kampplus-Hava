package com.kampplus.hava.data

import com.kampplus.hava.model.Forecast

open class WeatherRepository(
    private val forecastApi: ForecastApi = ApiClient.forecastApi,
) {
    open suspend fun getForecast(
        latitude: Double,
        longitude: Double,
        cityName: String,
    ): Result<Forecast> {
        return try {
            val response = forecastApi.getForecast(latitude = latitude, longitude = longitude)
            Result.success(response.toForecast(cityName = cityName))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

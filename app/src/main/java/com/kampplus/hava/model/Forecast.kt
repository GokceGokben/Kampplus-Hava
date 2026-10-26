package com.kampplus.hava.model

data class CurrentWeather(
    val temperature: Double,
    val apparentTemperature: Double,
    val relativeHumidity: Int,
    val isDay: Boolean,
    val precipitation: Double,
    val weatherCode: Int,
    val windSpeed: Double,
    val pressure: Double = 0.0,
    val visibilityKm: Double = 0.0,
    val uvIndex: Double = 0.0,
)

data class HourlyForecast(
    val time: String,
    val temperature: Double,
    val weatherCode: Int,
)

data class DailyForecast(
    val date: String,
    val maxTemp: Double,
    val minTemp: Double,
    val weatherCode: Int,
    val sunrise: String = "",
    val sunset: String = "",
)

data class Forecast(
    val cityName: String,
    val current: CurrentWeather,
    val hourly: List<HourlyForecast>,
    val daily: List<DailyForecast>,
)

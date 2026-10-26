package com.kampplus.hava.data

import com.google.gson.annotations.SerializedName
import com.kampplus.hava.model.CurrentWeather
import com.kampplus.hava.model.DailyForecast
import com.kampplus.hava.model.Forecast
import com.kampplus.hava.model.HourlyForecast

data class CurrentDto(
    @SerializedName("time") val time: String? = null,
    @SerializedName("temperature_2m") val temperature: Double = 0.0,
    @SerializedName("apparent_temperature") val apparentTemperature: Double = 0.0,
    @SerializedName("relative_humidity_2m") val relativeHumidity: Int = 0,
    @SerializedName("is_day") val isDay: Int = 1,
    @SerializedName("precipitation") val precipitation: Double = 0.0,
    @SerializedName("weather_code") val weatherCode: Int = 0,
    @SerializedName("wind_speed_10m") val windSpeed: Double = 0.0,
    @SerializedName("surface_pressure") val pressure: Double = 0.0,
    @SerializedName("visibility") val visibility: Double = 0.0,
    @SerializedName("uv_index") val uvIndex: Double = 0.0,
)

data class HourlyDto(
    @SerializedName("time") val time: List<String>? = null,
    @SerializedName("temperature_2m") val temperature: List<Double>? = null,
    @SerializedName("weather_code") val weatherCode: List<Int>? = null,
)

data class DailyDto(
    @SerializedName("time") val time: List<String>? = null,
    @SerializedName("temperature_2m_max") val maxTemp: List<Double>? = null,
    @SerializedName("temperature_2m_min") val minTemp: List<Double>? = null,
    @SerializedName("weather_code") val weatherCode: List<Int>? = null,
    @SerializedName("sunrise") val sunrise: List<String>? = null,
    @SerializedName("sunset") val sunset: List<String>? = null,
)

data class ForecastResponse(
    @SerializedName("latitude") val latitude: Double = 0.0,
    @SerializedName("longitude") val longitude: Double = 0.0,
    @SerializedName("current") val current: CurrentDto? = null,
    @SerializedName("hourly") val hourly: HourlyDto? = null,
    @SerializedName("daily") val daily: DailyDto? = null,
)

fun ForecastResponse.toForecast(cityName: String): Forecast {
    val currentData = current?.let {
        CurrentWeather(
            temperature = it.temperature,
            apparentTemperature = it.apparentTemperature,
            relativeHumidity = it.relativeHumidity,
            isDay = it.isDay == 1,
            precipitation = it.precipitation,
            weatherCode = it.weatherCode,
            windSpeed = it.windSpeed,
            pressure = it.pressure,
            visibilityKm = it.visibility / 1000.0,
            uvIndex = it.uvIndex,
        )
    } ?: CurrentWeather(
        temperature = 0.0,
        apparentTemperature = 0.0,
        relativeHumidity = 0,
        isDay = true,
        precipitation = 0.0,
        weatherCode = 0,
        windSpeed = 0.0,
    )

    val hourlyList = mutableListOf<HourlyForecast>()
    hourly?.let { h ->
        val times = h.time ?: emptyList()
        val temps = h.temperature ?: emptyList()
        val codes = h.weatherCode ?: emptyList()
        val count = minOf(times.size, temps.size, codes.size)
        // Saatlik liste gün başından gelir; yerel saatle (timezone=auto) "şu anki saat"ten başlatılır.
        val currentHour = current?.time?.take(13)

        for (i in 0 until count) {
            if (currentHour != null && times[i].take(13) < currentHour) continue
            val formattedTime = times[i].substringAfter("T").take(5)
            hourlyList.add(
                HourlyForecast(
                    time = formattedTime,
                    temperature = temps[i],
                    weatherCode = codes[i],
                ),
            )
        }
    }

    val dailyList = mutableListOf<DailyForecast>()
    daily?.let { d ->
        val times = d.time ?: emptyList()
        val maxs = d.maxTemp ?: emptyList()
        val mins = d.minTemp ?: emptyList()
        val codes = d.weatherCode ?: emptyList()
        val sunrises = d.sunrise ?: emptyList()
        val sunsets = d.sunset ?: emptyList()
        val count = minOf(times.size, maxs.size, mins.size, codes.size)

        for (i in 0 until count) {
            dailyList.add(
                DailyForecast(
                    date = times[i],
                    maxTemp = maxs[i],
                    minTemp = mins[i],
                    weatherCode = codes[i],
                    sunrise = sunrises.getOrNull(i)?.substringAfter("T")?.take(5).orEmpty(),
                    sunset = sunsets.getOrNull(i)?.substringAfter("T")?.take(5).orEmpty(),
                ),
            )
        }
    }

    return Forecast(
        cityName = cityName,
        current = currentData,
        hourly = hourlyList,
        daily = dailyList,
    )
}

package com.kampplus.hava.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.kampplus.hava.R

data class WeatherCondition(
    val code: Int,
    @param:DrawableRes val iconRes: Int,
    /** Türkçe yedek metin; arayüz yerelleştirilmiş [descriptionRes] kullanır. */
    val description: String,
    @param:StringRes val descriptionRes: Int,
) {
    companion object {
        fun fromWeatherCode(code: Int): WeatherCondition {
            return when (code) {
                0 -> WeatherCondition(code, R.drawable.ic_weather_clear, "Açık", R.string.weather_clear)
                1, 2, 3 -> WeatherCondition(code, R.drawable.ic_weather_partly_cloudy, "Parçalı Bulutlu", R.string.weather_partly_cloudy)
                45, 48 -> WeatherCondition(code, R.drawable.ic_weather_fog, "Sisli", R.string.weather_fog)
                51, 53, 55, 56, 57 -> WeatherCondition(code, R.drawable.ic_weather_rain, "Çiseleyen Yağmur", R.string.weather_drizzle)
                61, 63, 65, 66, 67, 80, 81, 82 -> WeatherCondition(code, R.drawable.ic_weather_rain, "Yağmurlu", R.string.weather_rain)
                71, 73, 75, 77, 85, 86 -> WeatherCondition(code, R.drawable.ic_weather_snow, "Kar Yağışlı", R.string.weather_snow)
                95, 96, 99 -> WeatherCondition(code, R.drawable.ic_weather_thunder, "Gökgürültülü Fırtına", R.string.weather_thunder)
                else -> WeatherCondition(code, R.drawable.ic_weather_unknown, "Bilinmeyen ($code)", R.string.weather_unknown)
            }
        }
    }
}

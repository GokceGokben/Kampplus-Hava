package com.kampplus.hava.model

import com.kampplus.hava.R
import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherConditionTest {

    @Test
    fun `fromWeatherCode returns correct condition for WMO 0`() {
        val condition = WeatherCondition.fromWeatherCode(0)
        assertEquals(R.drawable.ic_weather_clear, condition.iconRes)
        assertEquals("Açık", condition.description)
    }

    @Test
    fun `fromWeatherCode returns correct condition for rain WMO 61`() {
        val condition = WeatherCondition.fromWeatherCode(61)
        assertEquals(R.drawable.ic_weather_rain, condition.iconRes)
        assertEquals("Yağmurlu", condition.description)
    }

    @Test
    fun `fromWeatherCode returns correct condition for thunderstorm WMO 95`() {
        val condition = WeatherCondition.fromWeatherCode(95)
        assertEquals(R.drawable.ic_weather_thunder, condition.iconRes)
        assertEquals("Gökgürültülü Fırtına", condition.description)
    }

    @Test
    fun `fromWeatherCode handles unknown code gracefully`() {
        val condition = WeatherCondition.fromWeatherCode(999)
        assertEquals(R.drawable.ic_weather_unknown, condition.iconRes)
        assertEquals("Bilinmeyen (999)", condition.description)
    }
}

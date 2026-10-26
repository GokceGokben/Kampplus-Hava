package com.kampplus.hava.data

import org.junit.Assert.assertEquals
import org.junit.Test

class ForecastApiTest {

    @Test
    fun `toForecast correctly converts parallel columns into rows`() {
        val response = ForecastResponse(
            latitude = 39.92,
            longitude = 32.85,
            current = CurrentDto(
                temperature = 18.5,
                apparentTemperature = 17.2,
                relativeHumidity = 50,
                isDay = 1,
                precipitation = 0.0,
                weatherCode = 0,
                windSpeed = 10.0,
            ),
            hourly = HourlyDto(
                time = listOf("2025-02-28T00:00", "2025-02-28T01:00"),
                temperature = listOf(15.0, 14.5),
                weatherCode = listOf(0, 1),
            ),
            daily = DailyDto(
                time = listOf("2025-02-28", "2025-03-01"),
                maxTemp = listOf(20.0, 18.0),
                minTemp = listOf(10.0, 9.0),
                weatherCode = listOf(0, 61),
            ),
        )

        val forecast = response.toForecast("Ankara")

        assertEquals("Ankara", forecast.cityName)
        assertEquals(18.5, forecast.current.temperature, 0.01)
        assertEquals(true, forecast.current.isDay)

        // Test hourly matrix to row conversion
        assertEquals(2, forecast.hourly.size)
        assertEquals("00:00", forecast.hourly[0].time)
        assertEquals(15.0, forecast.hourly[0].temperature, 0.01)
        assertEquals(0, forecast.hourly[0].weatherCode)

        assertEquals("01:00", forecast.hourly[1].time)
        assertEquals(14.5, forecast.hourly[1].temperature, 0.01)
        assertEquals(1, forecast.hourly[1].weatherCode)

        // Test daily matrix to row conversion
        assertEquals(2, forecast.daily.size)
        assertEquals("2025-02-28", forecast.daily[0].date)
        assertEquals(20.0, forecast.daily[0].maxTemp, 0.01)
        assertEquals(10.0, forecast.daily[0].minTemp, 0.01)
        assertEquals(0, forecast.daily[0].weatherCode)
    }
}

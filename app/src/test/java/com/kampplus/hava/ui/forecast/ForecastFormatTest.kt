package com.kampplus.hava.ui.forecast

import com.kampplus.hava.R
import com.kampplus.hava.data.CurrentDto
import com.kampplus.hava.data.DailyDto
import com.kampplus.hava.data.ForecastResponse
import com.kampplus.hava.data.HourlyDto
import com.kampplus.hava.data.toForecast
import com.kampplus.hava.model.TemperatureUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Locale

class ForecastFormatTest {

    @Test
    fun `parseDayParts formats weekday and short date in Turkish`() {
        // 28 Şubat 2025 Cuma
        val parts = parseDayParts("2025-02-28", Locale.forLanguageTag("tr-TR"))
        assertEquals("Cuma", parts?.weekday)
        assertEquals("28 Şub", parts?.shortDate)
    }

    @Test
    fun `parseDayParts formats weekday in English`() {
        assertEquals("Friday", parseDayParts("2025-02-28", Locale.US)?.weekday)
    }

    @Test
    fun `parseDayParts returns null for invalid input`() {
        assertNull(parseDayParts("not-a-date", Locale.US))
    }

    @Test
    fun `uvLevelRes follows WHO ranges`() {
        assertEquals(R.string.uv_low, uvLevelRes(2.9))
        assertEquals(R.string.uv_moderate, uvLevelRes(3.0))
        assertEquals(R.string.uv_high, uvLevelRes(6.5))
        assertEquals(R.string.uv_very_high, uvLevelRes(9.0))
        assertEquals(R.string.uv_extreme, uvLevelRes(11.0))
    }

    @Test
    fun `temperature unit converts celsius to fahrenheit`() {
        assertEquals(32, TemperatureUnit.FAHRENHEIT.round(0.0))
        assertEquals(212, TemperatureUnit.FAHRENHEIT.round(100.0))
        assertEquals(22, TemperatureUnit.CELSIUS.round(21.6))
    }

    @Test
    fun `hourly list starts at the current local hour and maps extra fields`() {
        val response = ForecastResponse(
            current = CurrentDto(
                time = "2025-02-28T01:15",
                pressure = 1012.0,
                visibility = 24100.0,
                uvIndex = 4.0,
            ),
            hourly = HourlyDto(
                time = listOf("2025-02-28T00:00", "2025-02-28T01:00", "2025-02-28T02:00"),
                temperature = listOf(10.0, 11.0, 12.0),
                weatherCode = listOf(0, 0, 1),
            ),
            daily = DailyDto(
                time = listOf("2025-02-28"),
                maxTemp = listOf(20.0),
                minTemp = listOf(8.0),
                weatherCode = listOf(0),
                sunrise = listOf("2025-02-28T06:41"),
                sunset = listOf("2025-02-28T18:12"),
            ),
        )

        val forecast = response.toForecast("Ankara")

        assertEquals(listOf("01:00", "02:00"), forecast.hourly.map { it.time })
        assertEquals(24.1, forecast.current.visibilityKm, 0.001)
        assertEquals(1012.0, forecast.current.pressure, 0.001)
        assertEquals("06:41", forecast.daily[0].sunrise)
        assertEquals("18:12", forecast.daily[0].sunset)
    }
}

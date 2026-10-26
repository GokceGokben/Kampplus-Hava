package com.kampplus.hava.ui.forecast

import com.kampplus.hava.data.WeatherRepository
import com.kampplus.hava.model.CurrentWeather
import com.kampplus.hava.model.Forecast
import com.kampplus.hava.ui.LoadState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ForecastViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadForecast updates state with forecast data on success`() = runTest {
        val sampleForecast = Forecast(
            cityName = "Ankara",
            current = CurrentWeather(
                temperature = 20.0,
                apparentTemperature = 19.0,
                relativeHumidity = 40,
                isDay = true,
                precipitation = 0.0,
                weatherCode = 0,
                windSpeed = 10.0,
            ),
            hourly = emptyList(),
            daily = emptyList(),
        )

        val fakeRepo = object : FakeWeatherRepository() {
            override suspend fun getForecast(
                latitude: Double,
                longitude: Double,
                cityName: String,
            ): Result<Forecast> {
                return Result.success(sampleForecast)
            }
        }

        val viewModel = ForecastViewModel(fakeRepo)
        viewModel.loadForecast(cityName = "Ankara", latitude = 39.91, longitude = 32.85)
        advanceUntilIdle()

        val state = viewModel.loadState.value
        assertTrue(state is LoadState.Success)
        val forecast = (state as LoadState.Success).data
        assertEquals("Ankara", forecast.cityName)
        assertEquals(20.0, forecast.current.temperature, 0.01)
    }

    @Test
    fun `loadForecast updates state with Error on repository failure`() = runTest {
        val fakeRepo = object : FakeWeatherRepository() {
            override suspend fun getForecast(
                latitude: Double,
                longitude: Double,
                cityName: String,
            ): Result<Forecast> {
                return Result.failure(RuntimeException("Bağlantı hatası"))
            }
        }

        val viewModel = ForecastViewModel(fakeRepo)
        viewModel.loadForecast(cityName = "Ankara", latitude = 39.91, longitude = 32.85)
        advanceUntilIdle()

        val state = viewModel.loadState.value
        assertTrue(state is LoadState.Error)
        assertEquals("Bağlantı hatası", (state as LoadState.Error).message)
    }

    open class FakeWeatherRepository : WeatherRepository()
}

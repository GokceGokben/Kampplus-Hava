package com.kampplus.hava.ui.city

import com.kampplus.hava.data.CityRepository
import com.kampplus.hava.model.City
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
class CityViewModelTest {

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
    fun `initial state fetches sample cities successfully`() = runTest {
        val fakeRepo = object : FakeCityRepository() {
            override suspend fun search(query: String): Result<List<City>> {
                return Result.success(listOf(City(1, "Ankara", 39.9, 32.8)))
            }
        }
        val viewModel = CityViewModel(fakeRepo)
        advanceUntilIdle()

        val state = viewModel.loadState.value
        assertTrue(state is LoadState.Success)
        val cities = (state as LoadState.Success).data
        assertEquals(1, cities.size)
        assertEquals("Ankara", cities[0].name)
    }

    @Test
    fun `onQueryChange with no matches returns Empty state`() = runTest {
        val fakeRepo = object : FakeCityRepository() {
            override suspend fun search(query: String): Result<List<City>> {
                return Result.success(emptyList())
            }
        }
        val viewModel = CityViewModel(fakeRepo)
        advanceUntilIdle()

        viewModel.onQueryChange("BilinmeyenŞehir")
        advanceUntilIdle()

        val state = viewModel.loadState.value
        assertTrue(state is LoadState.Empty)
    }

    @Test
    fun `error in repository sets LoadState Error`() = runTest {
        val fakeRepo = object : FakeCityRepository() {
            override suspend fun search(query: String): Result<List<City>> {
                return Result.failure(RuntimeException("Ağ hatası"))
            }
        }
        val viewModel = CityViewModel(fakeRepo)
        advanceUntilIdle()

        val state = viewModel.loadState.value
        assertTrue(state is LoadState.Error)
        assertEquals("Ağ hatası", (state as LoadState.Error).message)
    }

    open class FakeCityRepository : CityRepository()
}

package com.kampplus.hava.data

import com.kampplus.hava.model.City
import com.kampplus.hava.model.TemperatureUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FavoritesRepositoryTest {

    private val ankara = City(id = 6, name = "Ankara", latitude = 39.9, longitude = 32.8, plaka = "06")
    private val izmir = City(id = 35, name = "İzmir", latitude = 38.4, longitude = 27.1, plaka = "35")

    @Test
    fun `toggle adds then removes a city`() {
        val repo = FavoritesRepository(InMemoryKeyValueStore())

        repo.toggle(ankara)
        assertTrue(repo.isFavorite("Ankara"))
        assertEquals(listOf("Ankara"), repo.favorites.value.map { it.name })

        repo.toggle(ankara)
        assertFalse(repo.isFavorite("Ankara"))
        assertTrue(repo.favorites.value.isEmpty())
    }

    @Test
    fun `favorites are persisted and restored in order`() {
        val store = InMemoryKeyValueStore()
        FavoritesRepository(store).apply {
            toggle(izmir)
            toggle(ankara)
        }

        val restored = FavoritesRepository(store).favorites.value
        assertEquals(listOf("İzmir", "Ankara"), restored.map { it.name })
        assertEquals(38.4, restored[0].latitude, 0.001)
        assertEquals("35", restored[0].plaka)
    }

    @Test
    fun `corrupted storage falls back to empty list`() {
        val store = InMemoryKeyValueStore().apply { put("favorite_cities", "{not json") }
        assertTrue(FavoritesRepository(store).favorites.value.isEmpty())
    }

    @Test
    fun `temperature unit toggle is persisted`() {
        val store = InMemoryKeyValueStore()
        val settings = SettingsRepository(store)
        assertEquals(TemperatureUnit.CELSIUS, settings.temperatureUnit.value)

        settings.toggleTemperatureUnit()

        assertEquals(TemperatureUnit.FAHRENHEIT, SettingsRepository(store).temperatureUnit.value)
    }
}

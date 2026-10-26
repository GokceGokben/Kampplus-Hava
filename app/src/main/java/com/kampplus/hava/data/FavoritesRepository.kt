package com.kampplus.hava.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kampplus.hava.model.City
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Favori şehirleri saklar. İller ada göre tekilleştirilir (API ve yerel listedeki id'ler farklı olabilir).
 */
class FavoritesRepository(
    private val store: KeyValueStore = InMemoryKeyValueStore(),
    private val gson: Gson = Gson(),
) {
    private val _favorites = MutableStateFlow(load())
    val favorites: StateFlow<List<City>> = _favorites.asStateFlow()

    fun isFavorite(cityName: String): Boolean = _favorites.value.any { it.name == cityName }

    fun toggle(city: City) {
        val current = _favorites.value
        val updated = if (current.any { it.name == city.name }) {
            current.filterNot { it.name == city.name }
        } else {
            current + city
        }
        _favorites.value = updated
        store.put(KEY, gson.toJson(updated))
    }

    private fun load(): List<City> {
        val json = store.get(KEY) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<City>>() {}.type
            gson.fromJson<List<City>>(json, type).orEmpty()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private companion object {
        const val KEY = "favorite_cities"
    }
}

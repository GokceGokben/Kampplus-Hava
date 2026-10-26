package com.kampplus.hava.ui.city

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kampplus.hava.data.CityRepository
import com.kampplus.hava.data.FavoritesRepository
import com.kampplus.hava.model.City
import com.kampplus.hava.model.nearestProvince
import com.kampplus.hava.ui.LoadState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class CityViewModel(
    private val cityRepository: CityRepository = CityRepository(),
    private val favoritesRepository: FavoritesRepository = FavoritesRepository(),
) : ViewModel() {

    val favorites: StateFlow<List<City>> = favoritesRepository.favorites

    fun toggleFavorite(city: City) {
        favoritesRepository.toggle(city)
    }

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    // Cihaz konumu alınana kadar öneri yoktur; sabit/sahte şehir gösterilmez
    private val _suggestedCity = MutableStateFlow<City?>(null)
    val suggestedCity: StateFlow<City?> = _suggestedCity.asStateFlow()

    private val _loadState = MutableStateFlow<LoadState<List<City>>>(LoadState.Loading)
    val loadState: StateFlow<LoadState<List<City>>> = _loadState.asStateFlow()

    private var searchJob: Job? = null

    init {
        searchCities("")
    }

    /**
     * Cihaz konumuna en yakın ili öneri olarak ayarlar; konum Türkiye dışındaysa öneri kaldırılır.
     * Öneri bulunduysa true döner.
     */
    fun suggestFromLocation(latitude: Double, longitude: Double): Boolean {
        val province = nearestProvince(latitude, longitude)
        _suggestedCity.value = province
        return province != null
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            searchCities(newQuery)
        }
    }

    fun clearQuery() {
        onQueryChange("")
    }

    fun retry() {
        searchCities(_query.value)
    }

    private fun searchCities(queryStr: String) {
        viewModelScope.launch {
            _loadState.value = LoadState.Loading
            val result = cityRepository.search(queryStr)
            result.fold(
                onSuccess = { cities ->
                    if (cities.isEmpty()) {
                        _loadState.value = LoadState.Empty
                    } else {
                        _loadState.value = LoadState.Success(cities)
                    }
                },
                onFailure = { error ->
                    _loadState.value = LoadState.Error(
                        message = error.localizedMessage.orEmpty(),
                        isNetwork = error is IOException,
                    )
                },
            )
        }
    }
}

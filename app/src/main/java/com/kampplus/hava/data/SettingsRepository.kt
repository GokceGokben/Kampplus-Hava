package com.kampplus.hava.data

import com.kampplus.hava.model.TemperatureUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(
    private val store: KeyValueStore = InMemoryKeyValueStore(),
) {
    private val _temperatureUnit = MutableStateFlow(
        store.get(KEY_UNIT)?.let { name -> TemperatureUnit.entries.firstOrNull { it.name == name } }
            ?: TemperatureUnit.CELSIUS,
    )
    val temperatureUnit: StateFlow<TemperatureUnit> = _temperatureUnit.asStateFlow()

    fun toggleTemperatureUnit() {
        val next = _temperatureUnit.value.toggled()
        _temperatureUnit.value = next
        store.put(KEY_UNIT, next.name)
    }

    private companion object {
        const val KEY_UNIT = "temperature_unit"
    }
}

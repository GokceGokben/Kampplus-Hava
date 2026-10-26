package com.kampplus.hava.model

import kotlin.math.roundToInt

enum class TemperatureUnit {
    CELSIUS,
    FAHRENHEIT,
    ;

    /** API her zaman °C döner; gösterim birimine çevrilir. */
    fun convert(celsius: Double): Double = when (this) {
        CELSIUS -> celsius
        FAHRENHEIT -> celsius * 9.0 / 5.0 + 32.0
    }

    fun round(celsius: Double): Int = convert(celsius).roundToInt()

    val symbol: String
        get() = when (this) {
            CELSIUS -> "°C"
            FAHRENHEIT -> "°F"
        }

    fun toggled(): TemperatureUnit = if (this == CELSIUS) FAHRENHEIT else CELSIUS
}

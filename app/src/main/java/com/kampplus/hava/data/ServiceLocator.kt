package com.kampplus.hava.data

import android.content.Context

/**
 * Favoriler ve ayarlar süreç boyunca tek örnek olmalı: şehir listesi ViewModel'i ile tahmin ekranı aynı akışı paylaşır.
 */
object ServiceLocator {
    private var favorites: FavoritesRepository? = null
    private var settings: SettingsRepository? = null

    @Synchronized
    fun favorites(context: Context): FavoritesRepository =
        favorites ?: FavoritesRepository(SharedPrefsKeyValueStore(context)).also { favorites = it }

    @Synchronized
    fun settings(context: Context): SettingsRepository =
        settings ?: SettingsRepository(SharedPrefsKeyValueStore(context)).also { settings = it }
}

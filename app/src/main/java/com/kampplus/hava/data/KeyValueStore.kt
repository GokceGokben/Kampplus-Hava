package com.kampplus.hava.data

import android.content.Context

/**
 * Küçük kalıcı anahtar-değer deposu. Repository'ler Context'e değil bu arayüze bağlıdır; böylece birim testlerde bellek içi sürüm kullanılır.
 */
interface KeyValueStore {
    fun get(key: String): String?
    fun put(key: String, value: String)
}

class InMemoryKeyValueStore : KeyValueStore {
    private val map = mutableMapOf<String, String>()
    override fun get(key: String): String? = map[key]
    override fun put(key: String, value: String) {
        map[key] = value
    }
}

class SharedPrefsKeyValueStore(context: Context) : KeyValueStore {
    private val prefs = context.applicationContext.getSharedPreferences("hava_prefs", Context.MODE_PRIVATE)
    override fun get(key: String): String? = prefs.getString(key, null)
    override fun put(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }
}

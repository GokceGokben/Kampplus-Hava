package com.kampplus.hava

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kampplus.hava.ui.HavaNavHost
import com.kampplus.hava.ui.theme.KampplusHavaTheme
import com.kampplus.hava.widget.WeatherSyncWorker

class MainActivity : ComponentActivity() {
    // Uygulama yalnızca Türkçe: cihaz dili ne olursa olsun kaynaklar ve tarih biçimleri Türkçe gelir
    override fun attachBaseContext(newBase: Context) {
        val config = Configuration(newBase.resources.configuration).apply {
            setLocale(java.util.Locale.forLanguageTag("tr-TR"))
        }
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WeatherSyncWorker.schedule(applicationContext)
        setContent {
            KampplusHavaTheme {
                HavaNavHost()
            }
        }
    }
}

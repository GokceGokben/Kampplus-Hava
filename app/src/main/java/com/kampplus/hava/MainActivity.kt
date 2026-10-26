package com.kampplus.hava

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kampplus.hava.ui.HavaNavHost
import com.kampplus.hava.ui.theme.KampplusHavaTheme
import com.kampplus.hava.widget.WeatherSyncWorker

class MainActivity : ComponentActivity() {
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

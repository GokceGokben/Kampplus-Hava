package com.kampplus.hava.data

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.CancellationSignal
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

/**
 * Cihazın yaklaşık konumunu verir. Google Play Hizmetleri gerektirmeyen platform LocationManager'ı kullanılır;
 * il düzeyinde öneri için yaklaşık (COARSE) konum yeterlidir.
 */
object LocationProvider {
    private const val FRESH_LOCATION_TIMEOUT_MS = 8_000L

    fun hasPermission(context: Context): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    suspend fun currentLocation(context: Context): Location? {
        if (!hasPermission(context)) return null
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return null
        val providers = try {
            manager.getProviders(true)
        } catch (e: Exception) {
            return null
        }

        // En yeni bilinen konum varsa hemen kullanılır
        val lastKnown = providers
            .mapNotNull { runCatching { manager.getLastKnownLocation(it) }.getOrNull() }
            .maxByOrNull { it.time }
        if (lastKnown != null) return lastKnown

        // Bilinen konum yoksa (API 30+) tek seferlik yeni konum istenir
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return null
        val provider = providers.firstOrNull { it != LocationManager.PASSIVE_PROVIDER } ?: return null
        return withTimeoutOrNull(FRESH_LOCATION_TIMEOUT_MS) {
            suspendCancellableCoroutine { continuation ->
                val signal = CancellationSignal()
                continuation.invokeOnCancellation { signal.cancel() }
                manager.getCurrentLocation(provider, signal, ContextCompat.getMainExecutor(context)) {
                    continuation.resume(it)
                }
            }
        }
    }
}

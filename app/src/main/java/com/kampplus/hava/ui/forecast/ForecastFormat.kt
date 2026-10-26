package com.kampplus.hava.ui.forecast

import androidx.annotation.StringRes
import com.kampplus.hava.R
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/** "2025-02-28" tarihinin yerelleştirilmiş parçaları: gün adı (Cuma) ve kısa tarih (28 Şub). */
data class DayParts(val weekday: String, val shortDate: String)

/**
 * Tarih, tahminin döndüğü yerel gün olarak yorumlanır; cihaz saat diliminden etkilenmemesi için UTC kullanılır.
 * Ayrıştırılamayan değerde null döner ve arayüz ham metni gösterir.
 */
fun parseDayParts(date: String, locale: Locale): DayParts? {
    val utc = TimeZone.getTimeZone("UTC")
    val parsed = try {
        SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = utc }.parse(date)
    } catch (e: Exception) {
        null
    } ?: return null
    val weekday = SimpleDateFormat("EEEE", locale).apply { timeZone = utc }.format(parsed)
    val shortDate = SimpleDateFormat("d MMM", locale).apply { timeZone = utc }.format(parsed)
    return DayParts(weekday.replaceFirstChar { it.titlecase(locale) }, shortDate)
}

/** WHO UV indeksi aralıkları. */
@StringRes
fun uvLevelRes(uv: Double): Int = when {
    uv < 3 -> R.string.uv_low
    uv < 6 -> R.string.uv_moderate
    uv < 8 -> R.string.uv_high
    uv < 11 -> R.string.uv_very_high
    else -> R.string.uv_extreme
}

fun Double.oneDecimal(locale: Locale = TurkishLocale): String = String.format(locale, "%.1f", this)

/** Uygulama yalnızca Türkçe olduğu için tarih ve sayı biçimleri cihaz diline bakmaz. */
val TurkishLocale: Locale = Locale.forLanguageTag("tr-TR")

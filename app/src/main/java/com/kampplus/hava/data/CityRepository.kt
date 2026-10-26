package com.kampplus.hava.data

import com.kampplus.hava.model.City
import com.kampplus.hava.model.sampleCities
import com.kampplus.hava.model.turkeyProvinces
import java.text.Collator
import java.util.Locale

open class CityRepository(
    private val cityApi: CityApi = ApiClient.cityApi,
) {
    private val trLocale = Locale.forLanguageTag("tr-TR")

    // Türkçe kurallarına (alfabeye) tam uygun A'dan Z'ye sıralama aracı
    private val collator = Collator.getInstance(trLocale).apply {
        strength = Collator.PRIMARY
    }

    private val byName = Comparator<City> { c1, c2 -> collator.compare(c1.name, c2.name) }

    /**
     * Türkçe karakterleri (ş, ğ, ç, ö, ü, ı) arama ve eşleştirme uyumluluğu için
     * İngilizce karşılıklarına dönüştürerek normalize eder.
     * Böylece "muğla", "mugla" ve "MUĞLA" aynı sonucu verir.
     */
    private fun String.normalizeForSearch(): String {
        return this.lowercase(trLocale)
            .replace("ğ", "g")
            .replace("ü", "u")
            .replace("ş", "s")
            .replace("ı", "i")
            .replace("ö", "o")
            .replace("ç", "c")
            .replace("i̇", "i")
            .removeSuffix(" ili")
            .trim()
    }

    /**
     * API sonucu bir il merkeziyse (PPLA) ilgili ili döner.
     * İl adı API'de farklı olabilir (örn: Hatay → "Antakya"), bu yüzden il, admin1 alanından bulunur;
     * koordinatlar API'den gelir.
     */
    private fun toProvince(apiCity: City): City? {
        if (apiCity.featureCode != "PPLA") return null
        val admin1Norm = apiCity.admin1?.normalizeForSearch() ?: return null
        val province = turkeyProvinces.firstOrNull { it.name.normalizeForSearch() == admin1Norm }
            ?: return null
        return province.copy(latitude = apiCity.latitude, longitude = apiCity.longitude)
    }

    /**
     * Yazılan harflere göre sıralar:
     * 1. Yazılanla başlayan iller (örn: 'Mu' → Muğla, Muş)
     * 2. Yazılanı içeren iller (örn: 'Mu' → Gümüşhane)
     * 3. API'nin bulduğu diğer iller (örn: 'Antakya' → Hatay)
     * Her grup kendi içinde Türkçe alfabetik sıralanır.
     */
    private fun List<City>.sortedByQuery(queryNorm: String): List<City> {
        return sortedWith(
            compareBy<City> {
                val index = it.name.normalizeForSearch().indexOf(queryNorm)
                when {
                    index == 0 -> 0
                    index > 0 -> 1
                    else -> 2
                }
            }.then(byName)
        )
    }

    private fun localMatches(queryNorm: String): List<City> {
        return turkeyProvinces.filter { it.name.normalizeForSearch().contains(queryNorm) }
    }

    open suspend fun search(query: String): Result<List<City>> {
        if (query.isBlank()) {
            // Boş durumda varsayılan şehirleri ALFABETİK olarak TR kurallarına göre sıralayıp gösterir
            return Result.success(sampleCities.sortedWith(byName))
        }

        val originalQuery = query.trim()
        val normalizedQuery = originalQuery.normalizeForSearch()

        return try {
            // Open-Meteo Türkçe karakterli ("muğla") ve karaktersiz ("mugla") aramayı destekliyor,
            // bu yüzden kullanıcının yazdığı haliyle gönderiyoruz.
            val response = cityApi.searchCities(name = originalQuery)
            val apiProvinces = (response.results ?: emptyList()).mapNotNull(::toProvince)

            // API 3 harften kısa aramalarda sadece tam eşleşme döndürüyor ("Va" → boş) ve bazı illeri
            // il adıyla bulamıyor (Kocaeli → İzmit). Eksik kalan illeri yerel 81 il listesinden tamamlıyoruz.
            val apiNames = apiProvinces.map { it.name }.toSet()
            val missing = localMatches(normalizedQuery).filter { it.name !in apiNames }

            val merged = (apiProvinces + missing).distinctBy { it.name }
            Result.success(merged.sortedByQuery(normalizedQuery))
        } catch (e: Exception) {
            // İnternet yoksa yerel 81 il listesinden getir
            val local = localMatches(normalizedQuery)
            if (local.isNotEmpty()) Result.success(local.sortedByQuery(normalizedQuery)) else Result.failure(e)
        }
    }
}

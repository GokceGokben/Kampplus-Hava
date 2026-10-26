package com.kampplus.hava.data

import com.kampplus.hava.model.City
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class CityRepositoryTest {

    private val country = "Türkiye Cumhuriyeti"

    private fun apiReturning(vararg cities: City) = object : CityApi {
        override suspend fun searchCities(name: String, count: Int, language: String, countryCode: String) =
            CitySearchResponse(results = cities.toList())
    }

    private val failingApi = object : CityApi {
        override suspend fun searchCities(name: String, count: Int, language: String, countryCode: String): CitySearchResponse =
            throw IOException("İnternet yok")
    }

    private suspend fun CityRepository.names(query: String) = search(query).getOrThrow().map { it.name }

    @Test
    fun `van province is found and other places are filtered out`() = runTest {
        val repo = CityRepository(
            apiReturning(
                City(1, "Van", 38.5012, 43.3729, country, "Van", featureCode = "PPLA"),
                City(2, "Van Ferit Melen Havaalanı", 38.46, 43.33, country, "Van", featureCode = "AIRP"),
                City(3, "Vank", 38.7, 39.1, country, "Elazığ", featureCode = "PPL"),
            )
        )
        val result = repo.search("Van").getOrThrow()
        assertEquals("Van", result.first().name)
        assertEquals(38.5012, result.first().latitude, 0.0001) // koordinat API'den gelir
        assertTrue(result.none { it.name == "Vank" || it.name.contains("Havaalanı") })
    }

    @Test
    fun `turkish characters and their ascii forms both match`() = runTest {
        val muglaApi = apiReturning(City(1, "Muğla", 37.2, 28.4, country, "Muğla", featureCode = "PPLA"))
        val repo = CityRepository(muglaApi)
        assertEquals(listOf("Muğla"), repo.names("muğla"))
        assertEquals(listOf("Muğla"), repo.names("mugla"))
        assertEquals(listOf("Muğla"), repo.names("MUĞLA"))
    }

    @Test
    fun `province with different center name is mapped via admin1`() = runTest {
        val repo = CityRepository(apiReturning(City(1, "Antakya", 36.2, 36.16, country, "Hatay", featureCode = "PPLA")))
        assertEquals(listOf("Hatay"), repo.names("Antakya"))
    }

    @Test
    fun `short queries are completed from local list and sorted by typed letters`() = runTest {
        // API 3 harften kısa aramalarda boş döner
        val repo = CityRepository(apiReturning())
        assertEquals(listOf("Muğla", "Muş", "Gümüşhane"), repo.names("mu"))
        assertEquals("Van", repo.names("va").first())
    }

    @Test
    fun `falls back to local list when api fails`() = runTest {
        val repo = CityRepository(failingApi)
        assertEquals(listOf("Muğla"), repo.names("muğla"))
        assertTrue(repo.search("BilinmeyenŞehir").isFailure)
    }
}

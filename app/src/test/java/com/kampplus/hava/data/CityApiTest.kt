package com.kampplus.hava.data

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class CityApiTest {

    @Test
    fun `gson deserializes CitySearchResponse correctly`() {
        val json = """
            {
                "results": [
                    {
                        "id": 3169070,
                        "name": "Ankara",
                        "latitude": 39.91987,
                        "longitude": 32.85427,
                        "country": "Türkiye",
                        "admin1": "Ankara",
                        "feature_code": "PPLA"
                    }
                ]
            }
        """.trimIndent()

        val gson = Gson()
        val response = gson.fromJson(json, CitySearchResponse::class.java)

        assertNotNull(response.results)
        assertEquals(1, response.results?.size)
        val city = response.results!![0]
        assertEquals(3169070L, city.id)
        assertEquals("Ankara", city.name)
        assertEquals(39.91987, city.latitude, 0.0001)
        assertEquals(32.85427, city.longitude, 0.0001)
        assertEquals("Türkiye", city.country)
        assertEquals("Ankara", city.admin1)
        assertEquals("PPLA", city.featureCode)
    }
}

package com.kampplus.hava.data

import com.google.gson.annotations.SerializedName
import com.kampplus.hava.model.City
import retrofit2.http.GET
import retrofit2.http.Query

data class CitySearchResponse(
    @SerializedName("results")
    val results: List<City>? = null,
)

interface CityApi {
    @GET("v1/search")
    suspend fun searchCities(
        @Query("name") name: String,
        @Query("count") count: Int = 100,
        @Query("language") language: String = "tr",
        // Sadece Türkiye sonuçları (yoksa "Van" aramasında Vancouver vb. listeyi dolduruyor)
        @Query("countryCode") countryCode: String = "TR",
    ): CitySearchResponse
}

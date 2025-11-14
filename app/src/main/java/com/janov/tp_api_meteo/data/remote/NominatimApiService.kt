package com.janov.tp_api_meteo.data.remote

import com.janov.tp_api_meteo.data.model.NominatimResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NominatimApiService {
    @GET("reverse")
    suspend fun reverseGeocode(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("format") format: String = "json",
        @Query("accept-language") language: String = "fr"
    ): Response<NominatimResponse>
}

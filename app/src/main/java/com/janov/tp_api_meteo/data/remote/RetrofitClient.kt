package com.janov.tp_api_meteo.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val GEOCODING_BASE_URL = "https://geocoding-api.open-meteo.com/"
    private const val WEATHER_BASE_URL = "https://api.open-meteo.com/"
    private const val NOMINATIM_BASE_URL = "https://nominatim.openstreetmap.org/"
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    // User-Agent interceptor for Nominatim (required by their API)
    private val userAgentInterceptor = okhttp3.Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("User-Agent", "WeatherApp/1.0 (Android)")
            .build()
        chain.proceed(request)
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val nominatimOkHttpClient = OkHttpClient.Builder()
        .addInterceptor(userAgentInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private fun createRetrofit(baseUrl: String, client: OkHttpClient = okHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    val geocodingApi: GeocodingApiService by lazy {
        createRetrofit(GEOCODING_BASE_URL).create(GeocodingApiService::class.java)
    }
    
    val weatherApi: WeatherApiService by lazy {
        createRetrofit(WEATHER_BASE_URL).create(WeatherApiService::class.java)
    }
    
    val nominatimApi: NominatimApiService by lazy {
        createRetrofit(NOMINATIM_BASE_URL, nominatimOkHttpClient).create(NominatimApiService::class.java)
    }
}

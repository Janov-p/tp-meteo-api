package com.janov.tp_api_meteo.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.google.gson.Gson
import com.janov.tp_api_meteo.data.local.WeatherDatabase
import com.janov.tp_api_meteo.data.local.entity.CityEntity
import com.janov.tp_api_meteo.data.local.entity.WeatherEntity
import com.janov.tp_api_meteo.data.model.GeocodingResult
import com.janov.tp_api_meteo.data.model.HourlyData
import com.janov.tp_api_meteo.data.remote.RetrofitClient
import com.janov.tp_api_meteo.domain.model.City
import com.janov.tp_api_meteo.domain.model.HourlyForecast
import com.janov.tp_api_meteo.domain.model.Weather
import com.janov.tp_api_meteo.domain.model.WeatherCondition
import com.janov.tp_api_meteo.util.NetworkError
import com.janov.tp_api_meteo.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class WeatherRepository(private val context: Context) {
    private val database = WeatherDatabase.getDatabase(context)
    private val cityDao = database.cityDao()
    private val weatherDao = database.weatherDao()
    private val geocodingApi = RetrofitClient.geocodingApi
    private val weatherApi = RetrofitClient.weatherApi
    private val nominatimApi = RetrofitClient.nominatimApi
    private val gson = Gson()
    
    companion object {
        private const val CACHE_EXPIRY_TIME = 30 * 60 * 1000L // 30 minutes
    }
    
    // Check if device has internet connection
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
    
    // Search cities via Geocoding API
    suspend fun searchCities(query: String): Result<List<City>> {
        if (!isNetworkAvailable()) {
            return Result.Error(NetworkError.NoInternet, "Pas de connexion internet")
        }
        
        return try {
            val response = geocodingApi.searchCity(query)
            if (response.isSuccessful) {
                val results = response.body()?.results ?: emptyList()
                val cities = results.map { it.toCity() }
                Result.Success(cities)
            } else {
                Result.Error(
                    NetworkError.ApiError(response.code(), response.message()),
                    "Erreur lors de la recherche: ${response.message()}"
                )
            }
        } catch (e: SocketTimeoutException) {
            Result.Error(NetworkError.Timeout, "Délai d'attente dépassé")
        } catch (e: UnknownHostException) {
            Result.Error(NetworkError.NoInternet, "Pas de connexion internet")
        } catch (e: Exception) {
            Result.Error(NetworkError.Unknown(e.message), "Erreur: ${e.message}")
        }
    }
    
    // Find nearest city by coordinates using reverse geocoding
    suspend fun findNearestCity(latitude: Double, longitude: Double): Result<City> {
        if (!isNetworkAvailable()) {
            return Result.Error(NetworkError.NoInternet, "Pas de connexion internet")
        }
        
        return try {
            // Use Nominatim API for reverse geocoding
            val response = nominatimApi.reverseGeocode(latitude, longitude)
            if (response.isSuccessful) {
                val nominatimResponse = response.body()
                if (nominatimResponse != null) {
                    val cityName = nominatimResponse.address?.getCityName() ?: "Position actuelle"
                    val country = nominatimResponse.address?.country
                    
                    val city = City(
                        id = "${latitude}_${longitude}",
                        name = cityName,
                        country = country,
                        latitude = latitude,
                        longitude = longitude,
                        isFavorite = false
                    )
                    Result.Success(city)
                } else {
                    // Fallback to coordinates if no result
                    val city = City(
                        id = "${latitude}_${longitude}",
                        name = "Position actuelle",
                        country = null,
                        latitude = latitude,
                        longitude = longitude,
                        isFavorite = false
                    )
                    Result.Success(city)
                }
            } else {
                Result.Error(
                    NetworkError.ApiError(response.code(), response.message()),
                    "Erreur lors de la recherche de la ville"
                )
            }
        } catch (e: SocketTimeoutException) {
            Result.Error(NetworkError.Timeout, "Délai d'attente dépassé")
        } catch (e: UnknownHostException) {
            Result.Error(NetworkError.NoInternet, "Pas de connexion internet")
        } catch (e: Exception) {
            // Fallback to coordinates on any error
            val city = City(
                id = "${latitude}_${longitude}",
                name = "Position actuelle",
                country = null,
                latitude = latitude,
                longitude = longitude,
                isFavorite = false
            )
            Result.Success(city)
        }
    }
    
    // Get weather for a city
    suspend fun getWeather(city: City, forceRefresh: Boolean = false): Result<Weather> {
        val cityId = city.id
        
        // Try to get from cache first if not forcing refresh
        if (!forceRefresh) {
            val cachedWeather = weatherDao.getWeatherByCityId(cityId)
            if (cachedWeather != null && !isCacheExpired(cachedWeather.timestamp)) {
                return Result.Success(cachedWeather.toWeather(city.name, true))
            }
        }
        
        // Fetch from API
        if (!isNetworkAvailable()) {
            // If no network, try to return cached data even if expired
            val cachedWeather = weatherDao.getWeatherByCityId(cityId)
            return if (cachedWeather != null) {
                Result.Success(cachedWeather.toWeather(city.name, true))
            } else {
                Result.Error(NetworkError.NoInternet, "Pas de connexion internet et aucune donnée en cache")
            }
        }
        
        return try {
            val response = weatherApi.getWeatherForecast(city.latitude, city.longitude)
            if (response.isSuccessful) {
                val weatherResponse = response.body()!!
                val weatherEntity = weatherResponse.toWeatherEntity(cityId)
                
                // Save to cache
                weatherDao.insertWeather(weatherEntity)
                
                // Save or update city
                val cityEntity = city.toCityEntity()
                cityDao.insertCity(cityEntity)
                
                Result.Success(weatherEntity.toWeather(city.name, false))
            } else {
                Result.Error(
                    NetworkError.ApiError(response.code(), response.message()),
                    "Erreur lors de la récupération de la météo"
                )
            }
        } catch (e: SocketTimeoutException) {
            Result.Error(NetworkError.Timeout, "Délai d'attente dépassé")
        } catch (e: UnknownHostException) {
            Result.Error(NetworkError.NoInternet, "Pas de connexion internet")
        } catch (e: Exception) {
            Result.Error(NetworkError.Unknown(e.message), "Erreur: ${e.message}")
        }
    }
    
    // Get favorite cities with their weather
    fun getFavoriteCitiesWithWeather(): Flow<List<Pair<City, Weather?>>> {
        return cityDao.getFavoriteCities().map { cities ->
            cities.map { cityEntity ->
                val city = cityEntity.toCity()
                val weather = weatherDao.getWeatherByCityId(cityEntity.id)?.toWeather(cityEntity.name, true)
                Pair(city, weather)
            }
        }
    }
    
    // Toggle favorite status
    suspend fun toggleFavorite(cityId: String, isFavorite: Boolean) {
        cityDao.updateFavoriteStatus(cityId, isFavorite)
    }
    
    // Get city by ID
    suspend fun getCityById(cityId: String): City? {
        return cityDao.getCityById(cityId)?.toCity()
    }
    
    // Clean expired cache
    suspend fun cleanExpiredCache() {
        val expiryTime = System.currentTimeMillis() - CACHE_EXPIRY_TIME
        weatherDao.deleteExpiredWeather(expiryTime)
    }
    
    private fun isCacheExpired(timestamp: Long): Boolean {
        return System.currentTimeMillis() - timestamp > CACHE_EXPIRY_TIME
    }
    
    // Extension functions for mapping
    private fun GeocodingResult.toCity(): City {
        val cityId = "${latitude}_${longitude}"
        return City(
            id = cityId,
            name = name,
            country = country,
            latitude = latitude,
            longitude = longitude,
            isFavorite = false
        )
    }
    
    private fun City.toCityEntity(): CityEntity {
        return CityEntity(
            id = id,
            name = name,
            country = country,
            latitude = latitude,
            longitude = longitude,
            isFavorite = isFavorite
        )
    }
    
    private fun CityEntity.toCity(): City {
        return City(
            id = id,
            name = name,
            country = country,
            latitude = latitude,
            longitude = longitude,
            isFavorite = isFavorite
        )
    }
    
    private fun com.janov.tp_api_meteo.data.model.WeatherResponse.toWeatherEntity(cityId: String): WeatherEntity {
        val currentIndex = 0 // Current hour
        val temperatures = hourly.temperature2m
        
        return WeatherEntity(
            cityId = cityId,
            latitude = latitude,
            longitude = longitude,
            currentTemperature = temperatures.getOrNull(currentIndex) ?: 0.0,
            currentHumidity = hourly.relativeHumidity2m.getOrNull(currentIndex) ?: 0,
            currentWindSpeed = hourly.windSpeed10m.getOrNull(currentIndex) ?: 0.0,
            currentRain = hourly.rain.getOrNull(currentIndex) ?: 0.0,
            minTemperature = temperatures.take(24).minOrNull() ?: 0.0,
            maxTemperature = temperatures.take(24).maxOrNull() ?: 0.0,
            hourlyDataJson = gson.toJson(hourly)
        )
    }
    
    private fun WeatherEntity.toWeather(cityName: String, isFromCache: Boolean): Weather {
        val hourlyData = gson.fromJson(hourlyDataJson, HourlyData::class.java)
        val hourlyForecasts = hourlyData.time.indices.take(24).map { i ->
            HourlyForecast(
                time = hourlyData.time[i],
                temperature = hourlyData.temperature2m[i],
                humidity = hourlyData.relativeHumidity2m[i],
                windSpeed = hourlyData.windSpeed10m[i],
                rain = hourlyData.rain[i]
            )
        }
        
        return Weather(
            cityId = cityId,
            cityName = cityName,
            latitude = latitude,
            longitude = longitude,
            currentTemperature = currentTemperature,
            currentHumidity = currentHumidity,
            currentWindSpeed = currentWindSpeed,
            currentRain = currentRain,
            minTemperature = minTemperature,
            maxTemperature = maxTemperature,
            hourlyForecasts = hourlyForecasts,
            weatherCondition = WeatherCondition.fromRainAndTemp(currentRain, currentTemperature),
            isFromCache = isFromCache,
            timestamp = timestamp
        )
    }
}

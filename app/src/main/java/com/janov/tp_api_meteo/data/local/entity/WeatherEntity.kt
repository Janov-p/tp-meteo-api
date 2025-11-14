package com.janov.tp_api_meteo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_cache")
data class WeatherEntity(
    @PrimaryKey
    val cityId: String, // Format: "latitude_longitude"
    val latitude: Double,
    val longitude: Double,
    val currentTemperature: Double,
    val currentHumidity: Int,
    val currentWindSpeed: Double,
    val currentRain: Double,
    val minTemperature: Double,
    val maxTemperature: Double,
    val hourlyDataJson: String, // JSON string of hourly data
    val timestamp: Long = System.currentTimeMillis()
)

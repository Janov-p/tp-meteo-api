package com.janov.tp_api_meteo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cities")
data class CityEntity(
    @PrimaryKey
    val id: String, // Format: "latitude_longitude"
    val name: String,
    val country: String?,
    val latitude: Double,
    val longitude: Double,
    val isFavorite: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)

package com.janov.tp_api_meteo.domain.model

data class City(
    val id: String,
    val name: String,
    val country: String?,
    val latitude: Double,
    val longitude: Double,
    val isFavorite: Boolean = false
)

package com.janov.tp_api_meteo.data.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("hourly")
    val hourly: HourlyData,
    @SerializedName("timezone")
    val timezone: String
)

data class HourlyData(
    @SerializedName("time")
    val time: List<String>,
    @SerializedName("temperature_2m")
    val temperature2m: List<Double>,
    @SerializedName("relative_humidity_2m")
    val relativeHumidity2m: List<Int>,
    @SerializedName("apparent_temperature")
    val apparentTemperature: List<Double>,
    @SerializedName("rain")
    val rain: List<Double>,
    @SerializedName("wind_speed_10m")
    val windSpeed10m: List<Double>
)

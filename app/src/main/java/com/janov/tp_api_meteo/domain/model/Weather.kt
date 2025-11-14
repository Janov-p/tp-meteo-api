package com.janov.tp_api_meteo.domain.model

data class Weather(
    val cityId: String,
    val cityName: String,
    val latitude: Double,
    val longitude: Double,
    val currentTemperature: Double,
    val currentHumidity: Int,
    val currentWindSpeed: Double,
    val currentRain: Double,
    val minTemperature: Double,
    val maxTemperature: Double,
    val hourlyForecasts: List<HourlyForecast>,
    val weatherCondition: WeatherCondition,
    val isFromCache: Boolean = false,
    val timestamp: Long
)

data class HourlyForecast(
    val time: String,
    val temperature: Double,
    val humidity: Int,
    val windSpeed: Double,
    val rain: Double
)

enum class WeatherCondition {
    SUNNY,
    PARTLY_CLOUDY,
    CLOUDY,
    RAINY,
    HEAVY_RAIN,
    STORMY;

    companion object {
        fun fromRainAndTemp(rain: Double, temperature: Double): WeatherCondition {
            return when {
                rain > 5.0 -> HEAVY_RAIN
                rain > 1.0 -> RAINY
                rain > 0.1 -> PARTLY_CLOUDY
                temperature > 20 -> SUNNY
                else -> CLOUDY
            }
        }
    }
}

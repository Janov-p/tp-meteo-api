package com.janov.tp_api_meteo.data.local.dao

import androidx.room.*
import com.janov.tp_api_meteo.data.local.entity.WeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather_cache WHERE cityId = :cityId")
    suspend fun getWeatherByCityId(cityId: String): WeatherEntity?
    
    @Query("SELECT * FROM weather_cache WHERE cityId = :cityId")
    fun getWeatherByCityIdFlow(cityId: String): Flow<WeatherEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity)
    
    @Query("DELETE FROM weather_cache WHERE cityId = :cityId")
    suspend fun deleteWeatherByCityId(cityId: String)
    
    @Query("DELETE FROM weather_cache WHERE timestamp < :expiryTime")
    suspend fun deleteExpiredWeather(expiryTime: Long)
    
    @Query("SELECT * FROM weather_cache")
    fun getAllWeather(): Flow<List<WeatherEntity>>
}

package com.janov.tp_api_meteo.data.local.dao

import androidx.room.*
import com.janov.tp_api_meteo.data.local.entity.CityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {
    @Query("SELECT * FROM cities WHERE isFavorite = 1 ORDER BY lastUpdated DESC")
    fun getFavoriteCities(): Flow<List<CityEntity>>
    
    @Query("SELECT * FROM cities WHERE id = :cityId")
    suspend fun getCityById(cityId: String): CityEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: CityEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCities(cities: List<CityEntity>)
    
    @Update
    suspend fun updateCity(city: CityEntity)
    
    @Delete
    suspend fun deleteCity(city: CityEntity)
    
    @Query("UPDATE cities SET isFavorite = :isFavorite WHERE id = :cityId")
    suspend fun updateFavoriteStatus(cityId: String, isFavorite: Boolean)
    
    @Query("DELETE FROM cities WHERE isFavorite = 0")
    suspend fun deleteNonFavorites()
}

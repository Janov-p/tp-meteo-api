package com.janov.tp_api_meteo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.janov.tp_api_meteo.data.local.dao.CityDao
import com.janov.tp_api_meteo.data.local.dao.WeatherDao
import com.janov.tp_api_meteo.data.local.entity.CityEntity
import com.janov.tp_api_meteo.data.local.entity.WeatherEntity

@Database(
    entities = [CityEntity::class, WeatherEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun cityDao(): CityDao
    abstract fun weatherDao(): WeatherDao
    
    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null
        
        fun getDatabase(context: Context): WeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    "weather_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

package com.janov.tp_api_meteo

import android.app.Application
import com.janov.tp_api_meteo.data.local.WeatherDatabase

class WeatherApplication : Application() {
    val database: WeatherDatabase by lazy { WeatherDatabase.getDatabase(this) }
    
    override fun onCreate() {
        super.onCreate()
    }
}

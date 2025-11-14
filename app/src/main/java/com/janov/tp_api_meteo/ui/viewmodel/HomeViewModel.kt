package com.janov.tp_api_meteo.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.janov.tp_api_meteo.data.repository.WeatherRepository
import com.janov.tp_api_meteo.domain.model.City
import com.janov.tp_api_meteo.domain.model.Weather
import com.janov.tp_api_meteo.util.Result
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val favoriteCities: List<Pair<City, Weather?>> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = WeatherRepository(application)
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadFavorites()
    }
    
    private fun loadFavorites() {
        viewModelScope.launch {
            repository.getFavoriteCitiesWithWeather()
                .catch { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
                .collect { favorites ->
                    _uiState.update {
                        it.copy(
                            favoriteCities = favorites,
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                }
        }
    }
    
    fun refreshWeather() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            
            val favorites = _uiState.value.favoriteCities
            favorites.forEach { (city, _) ->
                repository.getWeather(city, forceRefresh = true)
            }
            
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }
    
    fun removeFavorite(cityId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(cityId, false)
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

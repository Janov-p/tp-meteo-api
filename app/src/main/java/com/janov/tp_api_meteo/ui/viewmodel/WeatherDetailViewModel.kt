package com.janov.tp_api_meteo.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.janov.tp_api_meteo.data.repository.WeatherRepository
import com.janov.tp_api_meteo.domain.model.City
import com.janov.tp_api_meteo.domain.model.Weather
import com.janov.tp_api_meteo.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WeatherDetailUiState(
    val city: City? = null,
    val weather: Weather? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)

class WeatherDetailViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    private val repository = WeatherRepository(application)
    
    private val _uiState = MutableStateFlow(WeatherDetailUiState())
    val uiState: StateFlow<WeatherDetailUiState> = _uiState.asStateFlow()
    
    fun loadWeather(city: City) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, city = city, error = null) }
            
            when (val result = repository.getWeather(city)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            weather = result.data,
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            error = result.message ?: "Erreur lors du chargement",
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                }
                is Result.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }
    
    fun refreshWeather() {
        val city = _uiState.value.city ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, error = null) }
            
            when (val result = repository.getWeather(city, forceRefresh = true)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            weather = result.data,
                            isRefreshing = false
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            error = result.message ?: "Erreur lors du rafraîchissement",
                            isRefreshing = false
                        )
                    }
                }
                is Result.Loading -> {
                    _uiState.update { it.copy(isRefreshing = true) }
                }
            }
        }
    }
    
    fun toggleFavorite() {
        val city = _uiState.value.city ?: return
        viewModelScope.launch {
            val newFavoriteStatus = !city.isFavorite
            repository.toggleFavorite(city.id, newFavoriteStatus)
            _uiState.update {
                it.copy(city = city.copy(isFavorite = newFavoriteStatus))
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

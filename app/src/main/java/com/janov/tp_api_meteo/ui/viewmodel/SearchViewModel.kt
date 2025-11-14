package com.janov.tp_api_meteo.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.janov.tp_api_meteo.data.repository.LocationRepository
import com.janov.tp_api_meteo.data.repository.WeatherRepository
import com.janov.tp_api_meteo.domain.model.City
import com.janov.tp_api_meteo.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchUiState(
    val searchQuery: String = "",
    val searchResults: List<City> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingLocation: Boolean = false,
    val error: String? = null
)

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val weatherRepository = WeatherRepository(application)
    private val locationRepository = LocationRepository(application)
    
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
    
    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (query.length >= 2) {
            searchCities(query)
        } else {
            _uiState.update { it.copy(searchResults = emptyList()) }
        }
    }
    
    private fun searchCities(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = weatherRepository.searchCities(query)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            searchResults = result.data,
                            isLoading = false
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            error = result.message ?: "Erreur lors de la recherche",
                            isLoading = false
                        )
                    }
                }
                is Result.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }
    
    fun getCurrentLocation(onCityFound: (City) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingLocation = true, error = null) }
            
            when (val locationResult = locationRepository.getCurrentLocation()) {
                is Result.Success -> {
                    val (lat, lon) = locationResult.data
                    
                    // Find the nearest city using the coordinates
                    when (val cityResult = weatherRepository.findNearestCity(lat, lon)) {
                        is Result.Success -> {
                            _uiState.update { it.copy(isLoadingLocation = false) }
                            onCityFound(cityResult.data)
                        }
                        is Result.Error -> {
                            _uiState.update {
                                it.copy(
                                    error = cityResult.message ?: "Erreur lors de la recherche de la ville",
                                    isLoadingLocation = false
                                )
                            }
                        }
                        is Result.Loading -> {
                            _uiState.update { it.copy(isLoadingLocation = true) }
                        }
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            error = locationResult.message ?: "Erreur de géolocalisation",
                            isLoadingLocation = false
                        )
                    }
                }
                is Result.Loading -> {
                    _uiState.update { it.copy(isLoadingLocation = true) }
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

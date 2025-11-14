package com.janov.tp_api_meteo.util

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception, val message: String? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

sealed class NetworkError : Exception() {
    object NoInternet : NetworkError()
    object Timeout : NetworkError()
    object ServerError : NetworkError()
    data class ApiError(val code: Int, override val message: String) : NetworkError()
    data class Unknown(override val message: String?) : NetworkError()
}

package com.janov.tp_api_meteo.data.model

import com.google.gson.annotations.SerializedName

data class NominatimResponse(
    @SerializedName("address")
    val address: NominatimAddress?,
    @SerializedName("lat")
    val lat: String,
    @SerializedName("lon")
    val lon: String
)

data class NominatimAddress(
    @SerializedName("city")
    val city: String?,
    @SerializedName("town")
    val town: String?,
    @SerializedName("village")
    val village: String?,
    @SerializedName("municipality")
    val municipality: String?,
    @SerializedName("country")
    val country: String?,
    @SerializedName("country_code")
    val countryCode: String?
) {
    fun getCityName(): String {
        return city ?: town ?: village ?: municipality ?: "Position actuelle"
    }
}

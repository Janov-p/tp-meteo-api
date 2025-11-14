package com.janov.tp_api_meteo.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.janov.tp_api_meteo.domain.model.WeatherCondition

@Composable
fun WeatherIcon(
    condition: WeatherCondition,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified
) {
    val icon: ImageVector = when (condition) {
        WeatherCondition.SUNNY -> Icons.Default.WbSunny
        WeatherCondition.PARTLY_CLOUDY -> Icons.Default.Cloud
        WeatherCondition.CLOUDY -> Icons.Default.Cloud
        WeatherCondition.RAINY -> Icons.Default.WaterDrop
        WeatherCondition.HEAVY_RAIN -> Icons.Default.WaterDrop
        WeatherCondition.STORMY -> Icons.Default.Bolt
    }
    
    Icon(
        imageVector = icon,
        contentDescription = condition.name,
        modifier = modifier.size(48.dp),
        tint = tint
    )
}

@Composable
fun SmallWeatherIcon(
    condition: WeatherCondition,
    modifier: Modifier = Modifier
) {
    WeatherIcon(
        condition = condition,
        modifier = modifier.size(24.dp)
    )
}

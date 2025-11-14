package com.janov.tp_api_meteo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.janov.tp_api_meteo.domain.model.City
import com.janov.tp_api_meteo.ui.screen.HomeScreen
import com.janov.tp_api_meteo.ui.screen.SearchScreen
import com.janov.tp_api_meteo.ui.screen.WeatherDetailScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Search : Screen("search")
    object WeatherDetail : Screen("weather_detail/{cityJson}") {
        fun createRoute(city: City): String {
            val gson = Gson()
            val cityJson = gson.toJson(city)
            val encodedJson = URLEncoder.encode(cityJson, StandardCharsets.UTF_8.toString())
            return "weather_detail/$encodedJson"
        }
    }
}

@Composable
fun WeatherNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.route)
                },
                onNavigateToDetail = { city ->
                    navController.navigate(Screen.WeatherDetail.createRoute(city))
                }
            )
        }
        
        composable(Screen.Search.route) {
            SearchScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onCitySelected = { city ->
                    navController.navigate(Screen.WeatherDetail.createRoute(city)) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }
        
        composable(Screen.WeatherDetail.route) { backStackEntry ->
            val cityJson = backStackEntry.arguments?.getString("cityJson")
            val decodedJson = URLDecoder.decode(cityJson, StandardCharsets.UTF_8.toString())
            val gson = Gson()
            val city = gson.fromJson(decodedJson, City::class.java)
            
            WeatherDetailScreen(
                city = city,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

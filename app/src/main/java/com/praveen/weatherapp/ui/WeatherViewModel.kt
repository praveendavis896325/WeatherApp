package com.praveen.weatherapp.ui

import androidx.lifecycle.ViewModel
import com.praveen.weatherapp.data.network.Network

class WeatherViewModel(
    private val networkDataSource: Network,
    private val lat: Double,
    private val lon: Double
) : ViewModel() {
    val weather by lazyDeferred {
        networkDataSource.getWeatherData(lat,lon)
    }
}
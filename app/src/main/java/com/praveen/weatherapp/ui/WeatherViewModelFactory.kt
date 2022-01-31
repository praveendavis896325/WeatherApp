package com.praveen.weatherapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.praveen.weatherapp.data.network.Network

class WeatherViewModelFactory(
    private val networkDataSource: Network,
    private val lat: Double,
    private val lon: Double
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return WeatherViewModel(networkDataSource,lat,lon) as T
    }
}
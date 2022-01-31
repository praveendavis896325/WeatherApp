package com.praveen.weatherapp.data.network

import androidx.lifecycle.LiveData
import com.praveen.weatherapp.data.model.WeatherResponse

interface Network {
    val downloadedWeather: LiveData<WeatherResponse>

    suspend fun getWeatherData(
        lat: Double,
        lon:Double
    ): LiveData<WeatherResponse>
}
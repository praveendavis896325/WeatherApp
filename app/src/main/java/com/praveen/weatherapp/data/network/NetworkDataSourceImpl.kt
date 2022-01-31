package com.praveen.weatherapp.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.praveen.weatherapp.data.model.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NetworkDataSourceImpl(
    private val weatherService: APIService
) : Network {

    private val mutableDownloadedWeather = MutableLiveData<WeatherResponse>()
    override val downloadedWeather: LiveData<WeatherResponse>
        get() = mutableDownloadedWeather

    override suspend fun getWeatherData(lat: Double,lon: Double): LiveData<WeatherResponse> {
        return withContext(Dispatchers.IO) {
            fetchWeatherData(lat,lon)
            return@withContext downloadedWeather
        }
    }

    private suspend fun fetchWeatherData(lat: Double,lon: Double) {
        try {
            val fetchedWeatherValue = weatherService
                .getWeatherData(lat,lon).await()

            mutableDownloadedWeather.postValue(fetchedWeatherValue)
        } catch (e: NoInternetException) {
            Log.d("Network", "No Internet!")
        }
    }
}
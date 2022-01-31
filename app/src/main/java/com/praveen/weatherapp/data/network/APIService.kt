package com.praveen.weatherapp.data.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.praveen.weatherapp.BuildConfig
import com.praveen.weatherapp.data.model.WeatherResponse
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface APIService {

    @GET("weather")
    fun getWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Deferred<WeatherResponse>


    companion object {
        operator fun invoke(
            internetAvailabilityInterceptor: networkAvailabilityInterceptor
        ): APIService {

            // Interceptor to inject API key in each request
            val requestInterceptor = Interceptor { chain ->

                val url = chain.request()
                    .url
                    .newBuilder()
                    .addQueryParameter("APPID", BuildConfig.API_KEY)
                    .build()
                val request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()

                return@Interceptor chain.proceed(request)
            }

            // Add interceptor to show network log if in DEBUG build
            // Add requestInterceptor (API Key injector)
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(requestInterceptor)
                .addInterceptor(internetAvailabilityInterceptor)
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
                })
                .build()

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()
                .create(APIService::class.java)
        }
    }
}
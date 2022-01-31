package com.praveen.weatherapp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.praveen.weatherapp.R
import com.praveen.weatherapp.data.network.networkAvailabilityInterceptorImpl
import com.praveen.weatherapp.data.network.Network
import com.praveen.weatherapp.data.network.NetworkDataSourceImpl
import com.praveen.weatherapp.data.network.APIService
import com.praveen.weatherapp.utils.img_path
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class WeatherActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var job: Job
    private lateinit var viewModel: WeatherViewModel

    // TODO DI with Dagger2
    private lateinit var apiService: APIService
    private lateinit var networkDataSource: Network
    private lateinit var weatherViewModelFactory: WeatherViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        apiService = APIService(networkAvailabilityInterceptorImpl(this))
        networkDataSource = NetworkDataSourceImpl(apiService)
        weatherViewModelFactory = WeatherViewModelFactory(networkDataSource,51.5098,-0.1180)

        job = Job()

        viewModel = ViewModelProviders.of(this, weatherViewModelFactory)
            .get(WeatherViewModel::class.java)

        bind()
    }

    private fun bind() = launch {
        val temp = viewModel.weather.await()
        temp.observe(this@WeatherActivity, Observer {
            if (it == null) return@Observer

            group_loading.visibility = View.GONE
            showCity(it.name)
            showTemp(it.main.temp)
            showDesc(it.weather[0].description)
            showMinTemp(it.main.tempMin)
            showMaxTemp(it.main.tempMax)
            showIcon(it.weather[0].icon)
        })
    }

    private fun showCity(city: String) {
        textView_city.text = city
    }

    @SuppressLint("SetTextI18n")
    private fun showTemp(temp: Double) {
        textView_temperature.text = "$temp°C"
    }

    private fun showDesc(desc: String) {
        textView_description.text = desc
    }

    @SuppressLint("SetTextI18n")
    private fun showMinTemp(temp: Double) {
        textView_temp_min.text = "Min Temp: $temp°C"
    }

    @SuppressLint("SetTextI18n")
    private fun showMaxTemp(temp: Double) {
        textView_temp_max.text = "Max Temp: $temp°C"
    }

    private fun showIcon(code: String) {
        Glide.with(this@WeatherActivity)
            .load(img_path+"$code.png")
            .into(imageView_icon)
    }

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
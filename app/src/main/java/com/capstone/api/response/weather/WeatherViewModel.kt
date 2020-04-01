package com.capstone.api.response.weather

import Weather
import androidx.lifecycle.ViewModel

class WeatherViewModel : ViewModel() {
    private var weather : Weather? = null

    fun setWeather (initWeather: Weather?) {
        weather = initWeather
    }

    fun getWeather (): Weather? {
        return weather
    }
}
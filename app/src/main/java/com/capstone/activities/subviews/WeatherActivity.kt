package com.capstone.activities.subviews

import Weather
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.icu.util.UniversalTimeScale.toLong
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import com.capstone.R
import com.capstone.api.Retrofit2Client
import com.capstone.location.LocationProvider
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.weather_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Instant
import java.time.ZoneId

class WeatherActivity : AppCompatActivity() {
    private var weather: Weather? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather_activity)
        getWeather()
    }

    private fun getWeather () {
        val sharedPrefs = this.getSharedPreferences(
            getString(R.string.shared_preferences_key), Context.MODE_PRIVATE
        )
        var auth = sharedPrefs!!.getString(getString(R.string.access_token), "default")!!
        auth = "Bearer $auth"

        // using the location provider to get the long and lat, create the listener to set the viewModel
        LocationProvider.instance!!.init(this)
        LocationProvider.instance!!.getLocation()?.addOnSuccessListener { location: Location? ->
            Retrofit2Client.instance.getWeather(
                auth, location!!.longitude.toString(), location.latitude.toString()
            ).enqueue(object : Callback<Weather> {
                override fun onFailure(
                    call: Call<Weather>,
                    t: Throwable
                ) {
                    Log.d(TAG, "Unsuccessful weather call")
                }
                override fun onResponse(
                    call: Call<Weather>,
                    response: Response<Weather>
                ) {
                    if (response.code() == 200) {
                        weather = response.body()
                        Log.d(TAG, "Successful weather call")
                        populateWeather()
                    }
                }
            })
        }
    }

    private fun populateWeather() {
        //date time conversion from UNIX time to human readable
        val unixTime = weather?.currently?.time.toString().toLong()
        val updatedAt = Date(unixTime * 1000L).toString()
        val minTemp =
            "Min Temp: " + weather?.daily?.data?.get(0)?.temperatureMin.toString() + "°C"
        val maxTemp =
            "Max Temp: " + weather?.daily?.data?.get(0)?.temperatureMax.toString() + "°C"

        findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
        findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
        findViewById<TextView>(R.id.errorText).visibility = View.GONE
        findViewById<TextView>(R.id.updated_at).text = updatedAt
        findViewById<TextView>(R.id.status).text =
            weather?.currently?.summary.toString().capitalize()
        findViewById<TextView>(R.id.temp).text =
            weather?.currently?.temperature.toString()
        findViewById<TextView>(R.id.temp_min).text = minTemp
        findViewById<TextView>(R.id.temp_max).text = maxTemp
        findViewById<TextView>(R.id.precipitation).text =
            weather?.daily?.data?.get(0)?.precipType.toString()
        findViewById<TextView>(R.id.cloudCover).text =
            weather?.currently?.cloudCover.toString()
        findViewById<TextView>(R.id.precipProbability).text =
            weather?.currently?.precipProbability.toString()
        findViewById<TextView>(R.id.precipIntensity).text =
            weather?.currently?.precipIntensity.toString()
        findViewById<TextView>(R.id.humidity).text =
            weather?.currently?.humidity.toString()
        findViewById<TextView>(R.id.visible).text =
            weather?.currently?.visibility.toString()
        findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
        findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE
    }
}
private const val TAG = "WeatherActivity"

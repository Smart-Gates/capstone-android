package com.capstone.activities

import Weather
import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.icu.util.UniversalTimeScale.toLong
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
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
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.weather_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Instant
import java.time.ZoneId

class WeatherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather_activity)
        getWeather()
        // complete later
    }


    private fun getWeather() {
        // make sure app has gps permissions
        /** Finish check permissions
        if (!checkPermissions()) {
        return
        }**/
        // default values
        var lat = "43"
        var lon = "79"
        var locate = LocationServices.getFusedLocationProviderClient(this)
        /**  locate.lastLocation.addOnCompleteListener(this) { task ->
        var location: Location? = task.result
        lat = location?.latitude.toString()
        lon = location?.longitude.toString()

        }*/
        val sharedPref = this.getSharedPreferences(
            getString(R.string.shared_preferences_key), Context.MODE_PRIVATE
        )
        // exclamation marks is to ignore nullability
        var auth = sharedPref!!.getString(getString(R.string.access_token), "default")!!
        auth = "Bearer $auth"

        Retrofit2Client.instance.getWeather(auth, lat, lon)
            .enqueue(object : Callback<Weather> {
                override fun onFailure(call: Call<Weather>?, t: Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                    Toast.makeText(
                        applicationContext,
                        "Failed to get weather",
                        Toast.LENGTH_SHORT
                    ).show()
                }


                override fun onResponse(
                    call: Call<Weather>?,
                    response: Response<Weather>
                ) =// check to make sure that it was a successful return to server

                    if (response.code() == 200) {

                        //date time conversion from UNIX time to human readable
                        val unixTime = response.body()?.currently?.time.toString().toLong()
                        val updatedAt = Date(unixTime * 1000L).toString()
                        val minTemp =
                            "Min Temp: " + response.body()?.daily?.data?.get(0)?.temperatureMin.toString() + "°C"
                        val maxTemp =
                            "Max Temp: " + response.body()?.daily?.data?.get(0)?.temperatureMax.toString() + "°C"

                        findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
                        findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
                        findViewById<TextView>(R.id.errorText).visibility = View.GONE
                        findViewById<TextView>(R.id.updated_at).text = updatedAt
                        findViewById<TextView>(R.id.status).text =
                            response.body()?.currently?.summary.toString().capitalize()
                        findViewById<TextView>(R.id.temp).text =
                            response.body()?.currently?.temperature.toString()
                        findViewById<TextView>(R.id.temp_min).text = minTemp
                        findViewById<TextView>(R.id.temp_max).text = maxTemp
                        findViewById<TextView>(R.id.precipitation).text =
                            response.body()?.daily?.data?.get(0)?.precipType.toString()
                        findViewById<TextView>(R.id.cloudCover).text =
                            response.body()?.currently?.cloudCover.toString()
                        findViewById<TextView>(R.id.precipProbability).text =
                            response.body()?.currently?.precipProbability.toString()
                        findViewById<TextView>(R.id.precipIntensity).text =
                            response.body()?.currently?.precipIntensity.toString()
                        findViewById<TextView>(R.id.humidity).text =
                            response.body()?.currently?.humidity.toString()
                        findViewById<TextView>(R.id.visible).text =
                            response.body()?.currently?.visibility.toString()

                        findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                        findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE

                    } else
                        Toast.makeText(
                            applicationContext,
                            "Could not update weather",
                            Toast.LENGTH_LONG
                        ).show()
            })
    }

    // check the users gps permissions
    fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (permissionState == PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1
            )
        }
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

}
package com.capstone.utils.location

import android.content.Context
import android.location.Location
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task

class LocationProvider  {
    private var context: Context? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    fun init(
        context: Context?
    ) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
    }

    fun getLocation(): Task<Location>? {
        Log.i(TAG, "requestLocation")
        return fusedLocationClient.lastLocation
    }

    companion object {
        private const val TAG = "LocationProvider"
        private var INSTANCE: LocationProvider? = null
        val instance: LocationProvider?
            get() {
                if (INSTANCE == null) {
                    INSTANCE =
                        LocationProvider()
                }
                return INSTANCE
            }
    }
}
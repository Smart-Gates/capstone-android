package com.capstone.utils.location

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.capstone.utils.CapService
import com.capstone.R
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.synthetic.main.fragment_geofence.view.*


class GeofenceFragment : Fragment() {
    private lateinit var root : View
    private val runningAndroidQOrLater =
        android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q
    private val geofenceRadiusMeters = 200F
    private lateinit var geofencingClient: GeofencingClient
    private lateinit var geoPoint : GeoPoint

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // geofencing
        geofencingClient = LocationServices.getGeofencingClient(context!!)
        // Set a geofence based on the Users organization location
        createGeofenceOrg()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_geofence, container, false)

        root.updateNotificationButton.setOnClickListener {
            Log.d(TAG, "Enter Text ${root.enterText.text} exit text ${root.exitText.text}")
            // if either are null then reset the geofence
            if(root.enterText.text.toString().trim().isEmpty() || root.exitText.text.toString().trim().isEmpty()) {
                removeGeofences()
            }
            if(root.enterText.text.toString().trim().isNotEmpty()) {
                storeText(root.enterText.text.toString(), 1)
                addGeofence(geoPoint!!.latitude, geoPoint!!.longitude, "1", Geofence.GEOFENCE_TRANSITION_ENTER)
            }
            if(root.exitText.text.toString().trim().isNotEmpty()) {
                storeText(root.exitText.text.toString(), 2)
                addGeofence(geoPoint!!.latitude, geoPoint!!.longitude, "2", Geofence.GEOFENCE_TRANSITION_EXIT)
            }
        }
        applyStoreText()

        return root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "Checking Permissions")
        checkPermissionsGeofencing()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun checkPermissionsGeofencing() {
        if (locationPermissionApproved()) {
            return
        }
        // request permission
        var permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

        val resultCode = when {
            runningAndroidQOrLater -> {
                permissionsArray += Manifest.permission.ACCESS_BACKGROUND_LOCATION
                REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
            }
            else -> REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
        }

        Log.d(TAG, "Request foreground only location permission")
        ActivityCompat.requestPermissions(
            this.activity!!,
            permissionsArray,
            resultCode
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun locationPermissionApproved(): Boolean {
        val fgPermission = (PackageManager.PERMISSION_GRANTED ==
                ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION))
        val bgPermission =
            if (runningAndroidQOrLater) {
                PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                    context!!, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            } else {
                true
            }
        return fgPermission && bgPermission
    }
    private fun createGeofenceOrg() {
        Log.d(TAG, "Add geofence for org")

        val sharedPref =
            this.activity!!.getSharedPreferences(
                getString(R.string.shared_preferences_key),
                Context.MODE_PRIVATE
            )
        val orgAddress = sharedPref.getString(getString(R.string.org_address), null)

        if(orgAddress.equals(null)){
            Log.d(TAG, "no org address")
            return
        }
        geoPoint = CapService().getLocationFromAddress(orgAddress!!, context!!)!!
        Log.d(TAG, "Geopoint is: $geoPoint")
    }

    // add a geofence given the set values
    private fun addGeofence(lat: Double, long: Double, index: String, transitionType: Int) {
        val geofence = Geofence.Builder()
            .setRequestId(index)
            .setCircularRegion(lat, long, geofenceRadiusMeters)
            .setExpirationDuration(999999999999999999)
            .setTransitionTypes(transitionType)
            .build()

        // Build the geofence request
        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()
        Log.d(TAG,"Build geofence")


        Log.d(TAG,"after remove geofence")

        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)?.run {
            addOnSuccessListener {
                Log.d(TAG, "Add geofence "+ geofence.requestId)
            }
            addOnFailureListener {
                Log.d(TAG, "failed Add geofence "+geofence.requestId)
            }
        }
    }

    private fun removeGeofences() {
        geofencingClient.removeGeofences(geofencePendingIntent)?.run {
            addOnSuccessListener {
                // Geofences removed
                Log.d(TAG, "geofence removed")
            }
            addOnFailureListener {
                // Failed to remove geofences
                Log.d(TAG, "geofence removed fail")
            }
        }
    }

    private fun storeText(text: String, type: Int) {
        val sharedPref =
            this.activity!!.getSharedPreferences(
                getString(R.string.shared_preferences_key),
                Context.MODE_PRIVATE
            ).edit()
        if(type == 1)
            sharedPref.putString(getString(R.string.geofence_enter_store), text)
        else
            sharedPref.putString(getString(R.string.geofence_exit_store), text)
        sharedPref.apply()
    }

    private fun applyStoreText() {
        val sharedPref =
            this.activity!!.getSharedPreferences(
                getString(R.string.shared_preferences_key),
                Context.MODE_PRIVATE
            )

        val enterText = sharedPref.getString(getString(R.string.geofence_enter_store), "")
        val exitText  = sharedPref.getString(getString(R.string.geofence_exit_store), "")
        root.enterText.setText(enterText.toString())
        root.exitText.setText(exitText.toString())
    }
}

private const val TAG = "GeofenceFragment"
private const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 33
private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34

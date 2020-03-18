package com.capstone

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.capstone.notifications.GeofenceBroadcastReceiver
import com.capstone.notifications.MyFirebaseMessagingService
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private val runningAndroidQOrLater =
        android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q
    private val geofenceRadiusMeters = 100F
    private lateinit var geofencingClient: GeofencingClient
   // val geofenceList: MutableList<Geofence>
    var geofenceList: MutableList<Geofence> = ArrayList()

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // SET firebase messaging
        MyFirebaseMessagingService().initFirebase(this)
        // create the notification channel
        createChannel()
        // geofencing
        geofencingClient = LocationServices.getGeofencingClient(this)
        //check log in status
        val sharedPref = this.getSharedPreferences(
            getString(R.string.shared_preferences_key), Context.MODE_PRIVATE
        )
        val isLoggedIn = sharedPref.getBoolean(getString(R.string.is_logged_in_key), false)

        if (!isLoggedIn) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        // get the events/reminders
        CapService().getEventReminders(this)


        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "Checking Permissions")
        checkPermissionsGeofencing()

        // add the geofence
        addGeofence(43.657738,  -79.377298, "1")
    }

    // used to crate a notification channel with the visual and auditory behaviours
    private fun createChannel() {
        //create a channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                getString(R.string.notification_channel_id),
                getString(R.string.notification_channel_name),
                // notification importance
                NotificationManager.IMPORTANCE_HIGH
            )// disable badges for this channel
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_channel_description)

            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun checkPermissionsGeofencing(){
        if (locationPermissionApproved()){
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
            this@MainActivity,
            permissionsArray,
            resultCode
        )

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun locationPermissionApproved(): Boolean {
        val fgPermission = (PackageManager.PERMISSION_GRANTED ==
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION))
        val bgPermission =
            if (runningAndroidQOrLater) {
                PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_BACKGROUND_LOCATION )
            } else {
                true
            }
        return fgPermission && bgPermission
    }

    private fun addGeofence(lat: Double, long: Double, index: String) {
        // Set the request ID of the geofence. This is a string to identify this
        // geofence.

        /*geofenceList.add(
            Geofence.Builder()
                .setRequestId(index)
                .setCircularRegion(lat, long, geofenceRadiusMeters)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setExpirationDuration(999999999999999999)
                .build()
        )
        val geofencingRequest = getGeofencingRequest()
        removeExisitngAndAddGeofence(geofencingRequest)
        */
        removeGeofences()
        val geofence = Geofence.Builder()
            // Set the request ID, string to identify the geofence.
            .setRequestId(index)
            // Set the circular region of this geofence.
            .setCircularRegion(lat,long,geofenceRadiusMeters )
            // Set the expiration duration of the geofence. This geofence gets
            // automatically removed after this period of time.
            .setExpirationDuration(999999999999999999)
            // Set the transition types of interest. Alerts are only generated for these
            // transition. We track entry and exit transitions in this sample.
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()

        // Build the geofence request
        val geofencingRequest = GeofencingRequest.Builder()
            // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
            // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
            // is already inside that geofence.
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)

            // Add the geofences to be monitored by geofencing service.
            .addGeofence(geofence)
            .build()

        // First, remove any existing geofences that use our pending intent
        geofencingClient.removeGeofences(geofencePendingIntent)?.run {
            // Regardless of success/failure of the removal, add the new geofence
            addOnCompleteListener {
                // Add the new geofence request with the new geofence
                geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)?.run {
                    addOnSuccessListener {
                        // Geofences added.

                        Log.e("Add Geofence", geofence.requestId)
                        // Tell the viewmodel that we've reached the end of the game and
                        // activated the last "geofence" --- by removing the Geofence.
                    }
                    addOnFailureListener {
                        // Failed to add geofences.

                        if ((it.message != null)) {
                            Log.w(TAG, it.message)
                        }
                    }
                }
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

    private fun getGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(geofenceList)
        }.build()
    }

    // removes existing geofences that share the pending request
    private fun removeExisitngAndAddGeofence(geofencingRequest: GeofencingRequest) {
        geofencingClient?.removeGeofences(geofencePendingIntent)?.run {
            addOnSuccessListener {
                Log.d(TAG,"Geofence removed")
                Toast.makeText(applicationContext, "Geofence Removed", Toast.LENGTH_SHORT)
                    .show()
                geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)?.run {
                    addOnSuccessListener {
                        // Geofences added.
                        Toast.makeText(this@MainActivity, "Geofence Added",
                            Toast.LENGTH_SHORT)
                            .show()
                        Log.d(TAG, "Geofence Added"+geofencingRequest.toString())

                    }
                    addOnFailureListener {
                        // Failed to add geofences.
                        Log.d(TAG, "Geofence NotAdded")

                    }
                }




            }
            addOnFailureListener {
                Log.d(TAG, "Failed to remove geofence")
                Toast.makeText(applicationContext,"Failed to remove geofence", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}

private const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 33
private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
private const val TAG = "MainActivity"

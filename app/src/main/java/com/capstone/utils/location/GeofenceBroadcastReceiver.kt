package com.capstone.utils.location

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.capstone.R
import com.capstone.utils.notifications.sendNotification
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "Geofence recieved")

        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, errorMessage)
            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition

        // Test that the reported transition was of interest.
        val notificationManager = context!!.getSystemService(
            NotificationManager::class.java
        )

        val sharedPref =
            context.getSharedPreferences(
                context.getString(R.string.shared_preferences_key),
                Context.MODE_PRIVATE
            )
        when (geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                val enterText = sharedPref.getString(context.getString(R.string.geofence_enter_store), "")

                notificationManager?.sendNotification(
                    99999999,
                    "You've Entered The Gate!",
                    enterText!!,
                    context
                )
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                val exitText  = sharedPref.getString(context.getString(R.string.geofence_exit_store), "")

                notificationManager?.sendNotification(
                    99999999,
                    "You've Left The Gate!",
                    exitText!!,
                    context
                )
            }
            else -> {
                Log.e(TAG, "Geofence transition is invalid")
            }
        }
    }
}

private const val TAG = "GeofenceBroadcastReceiver"


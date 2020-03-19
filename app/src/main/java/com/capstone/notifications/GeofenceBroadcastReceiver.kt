package com.capstone.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
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
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            val triggeringGeofences = geofencingEvent.triggeringGeofences

            // Get the transition details as a String.
            val geofenceTransitionDetails = getGeofenceTransitionDetails(
                this,
                geofenceTransition,
                triggeringGeofences
            )

            // Send notification and log the transition details.


            notificationManager?.sendNotification(
                99999999,
                "You've Entered The Gate!",
                "The Geofence has been passed",
                context
            )
            Log.i(TAG, geofenceTransitionDetails.toString())
        } else if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){
            notificationManager?.sendNotification(
                99999999,
                "You've Left The Gate!",
                "Remember your: keys, ID card, and bag",
                context
            )
        }

        else {
            Log.e(TAG, "Geofence transition is invalid")
        }
    }
    fun getGeofenceTransitionDetails(broadcastReceiver: GeofenceBroadcastReceiver, transitionType: Int, triggeringGeofences: MutableList<Geofence>){

    }

}

private const val TAG = "GeofenceBroadcastReceiver"


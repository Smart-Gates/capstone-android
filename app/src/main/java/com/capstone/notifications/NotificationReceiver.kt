package com.capstone.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

// used for the creation of notification alarms to be set in a future date
class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // sendNotification
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager


        notificationManager.sendNotification(
            intent.getIntExtra("notificationId", 0),
            intent.getStringExtra("notificationTitle")!!,
            intent.getStringExtra("notificationBody")!!,
            context
        )

    }

}
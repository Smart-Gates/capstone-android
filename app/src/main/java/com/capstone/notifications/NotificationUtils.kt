package com.capstone.notifications

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationCompat
import com.capstone.R
import com.capstone.MainActivity
import com.capstone.models.DisplayNotification

/** Utility to create and send notifications
 * if more than one notification with the same ID is created, the newly created notification will
 * destroy the older one as it will not be displayed
 * */
fun NotificationManager.sendNotification(
    notificationId: Int,
    messageTitle: String,
    messageBody: String,
    applicationContext: Context
) {
    // Create the content intent for the notification which launches the MainActivity

    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        notificationId,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val largeImage = BitmapFactory.decodeResource(
        applicationContext.resources,
        android.R.mipmap.sym_def_app_icon
    )
    val bigPicStyle = NotificationCompat.BigPictureStyle()
        .bigPicture(largeImage)
        .bigLargeIcon(null)


    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_id)
    )
        .setSmallIcon(R.drawable.ic_stat_r)
        .setContentTitle(messageTitle)
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .setStyle(bigPicStyle)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
    notify(notificationId, builder.build())
}

/**
 * Cancels all notifications.
 *
 */
fun NotificationManager.cancelNotifications() {
    cancelAll()
}

// creates a Alarm that wakes up the device to
fun NotificationManager.displayNotificationLater(
    displayNotification: DisplayNotification,
    applicationContext: Context
) {
    val notifyIntent = Intent(applicationContext, NotificationReceiver::class.java)
    notifyIntent.putExtra("notificationId", displayNotification.notificationId)
    notifyIntent.putExtra("notificationTitle", displayNotification.messageTitle)
    notifyIntent.putExtra("notificationBody", displayNotification.messageBody)


    val notifyPendingIntent = PendingIntent.getBroadcast(
        applicationContext,
        displayNotification.notificationId,
        notifyIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
    val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    AlarmManagerCompat.setExactAndAllowWhileIdle(
        alarmManager,
        AlarmManager.RTC_WAKEUP,
        displayNotification.timeInMs,
        notifyPendingIntent
    )
}
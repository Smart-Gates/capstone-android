package com.capstone

import android.Manifest
import android.app.Activity
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.capstone.api.Retrofit2Client
import com.capstone.models.DisplayNotification
import com.capstone.models.FCMTokenPayload
import com.capstone.models.FCMTokenResponse
import com.capstone.models.events.EventList
import com.capstone.models.reminders.ReminderList
import com.capstone.notifications.displayNotificationLater
import com.capstone.notifications.sendNotification
import com.google.firebase.iid.FirebaseInstanceId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


internal class CapService : Service() {
    private val TAG = "CapService"


    fun checkPermissions(activity: Activity): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (permissionState == PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1
            )
        }
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    fun deleteFirebaseToken() {
        val thread = Thread(Runnable {
            try {
                FirebaseInstanceId.getInstance().deleteInstanceId()
            } catch (e: Exception) {
                Log.e(TAG, Log.getStackTraceString(e))
            }
        })
        thread.start()
    }

    fun setFCMTokenToServer(token: String, auth: String) {
        val payload = FCMTokenPayload(token)
        Log.d(TAG, "token to be sent $token")

        Retrofit2Client.instance.updateFCMToken(auth, payload)
            .enqueue(object : Callback<FCMTokenResponse> {
                override fun onFailure(call: Call<FCMTokenResponse>?, t: Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(
                    call: Call<FCMTokenResponse>?,
                    response: Response<FCMTokenResponse>
                ) {
                    Log.d(TAG, response.body().toString())
                }
            })
    }

    fun getEvents(auth: String, mContext: Context) {
        Retrofit2Client.instance.getEvents(auth)
            .enqueue(object : Callback<EventList> {
                override fun onFailure(call: Call<EventList>?, t: Throwable) {
                    Log.d("LOG_TAG_CHECK", t.toString())
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(
                    call: Call<EventList>?,
                    response: Response<EventList>
                ) {
                    // check to make sure that it was a successful return to server
                    if (response.code() == 200) {
                        // begin generating the alarm notifications from the API response
                        generateEventAlarmNotifications(response?.body()!!, mContext)
                    }
                }
            })
    }

    fun getReminders(auth: String, mContext: Context) {
        Retrofit2Client.instance.getReminders(auth)
            .enqueue(object : Callback<ReminderList> {
                override fun onFailure(call: Call<ReminderList>?, t: Throwable) {
                    Log.d("LOG_TAG_CHECK", t.toString())
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(
                    call: Call<ReminderList>?,
                    response: Response<ReminderList>
                ) {
                    // check to make sure that it was a successful return to server
                    if (response.code() == 200) {
                        // begin generating the alarm notifications from the API response
                        generateReminderAlarmNotifications(response?.body()!!, mContext)
                    }
                }
            })
    }

    private fun generateEventAlarmNotifications(eventsList: EventList, mContext: Context) {

        eventsList.content.forEach { event ->
            val start = event.start_time
            val alarmT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA).parse(start)!!.time
            val timeNow = Calendar.getInstance().timeInMillis
            val diff = alarmT - timeNow

            //create new alarms for future
            if (diff > 0) {
                Log.d(TAG, "Creating new event @$start in $diff ms")
                val displayNotification =
                    DisplayNotification(event.id.toInt(), event.title, event.description, alarmT)
                displayNotificationLater(displayNotification, mContext)
            }
            Log.d("LOG_TAG_CHECK", "Past Start Time @$start event notification not created")

        }
    }

    private fun generateReminderAlarmNotifications(reminderList: ReminderList, mContext: Context) {

        reminderList.content.forEach { reminder ->
            val start = reminder.start_time
            val alarmT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA).parse(start)!!.time
            val timeNow = Calendar.getInstance().timeInMillis
            val diff = alarmT - timeNow

            //create new alarms for future
            if (diff > 0) {
                Log.d(TAG, "Creating new reminder @$start in $diff ms")
                val displayNotification =
                    DisplayNotification(reminder.id.toInt(), reminder.title, reminder.description, alarmT)
                displayNotificationLater(displayNotification, mContext)
            }
            Log.d(TAG, "Past Start Time @$start reminder notification not created")

        }
    }

    private fun displayNotificationNow() {
        val notificationManager = getSystemService(
            NotificationManager::class.java
        )

        notificationManager?.sendNotification(
            1,
            "Project Update Meeting",
            "Meeting Room 3\nThis meeting will go over any changes that have been made",
            applicationContext
        )
    }

    private fun displayNotificationLater(
        displayNotification: DisplayNotification, mContext: Context
    ) {
        val notificationManager = mContext.getSystemService(
            NotificationManager::class.java
        )

        notificationManager?.displayNotificationLater(
            displayNotification,
            mContext
        )
    }



}


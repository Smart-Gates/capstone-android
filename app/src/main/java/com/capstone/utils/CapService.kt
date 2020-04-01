package com.capstone.utils

import android.Manifest
import android.app.Activity
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.capstone.R
import com.capstone.api.Retrofit2Client
import com.capstone.api.response.DisplayNotification
import com.capstone.api.request.FCMTokenPayload
import com.capstone.api.response.FCMTokenResponse
import com.capstone.api.response.OrganizationResponse
import com.capstone.api.response.events.EventList
import com.capstone.api.response.reminders.ReminderList
import com.capstone.utils.notifications.displayNotificationLater
import com.capstone.utils.notifications.sendNotification
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.iid.FirebaseInstanceId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


internal class CapService : Service() {

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
                    DisplayNotification(
                        event.id.toInt(),
                        event.title,
                        event.description,
                        alarmT
                    )
                displayNotificationLater(displayNotification, mContext)
            }
            else{
                Log.d(TAG, "Past Start Time @$start event notification not created")
            }
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
                    DisplayNotification(
                        reminder.id.toInt(),
                        reminder.title,
                        reminder.description,
                        alarmT
                    )
                displayNotificationLater(displayNotification, mContext)
            }
            else{
                Log.d(TAG, "Past Start Time @$start reminder notification not created")
            }
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

    fun getEventReminders(context: Context) {
        val prefs = context.getSharedPreferences(
            context.getString(R.string.shared_preferences_key),
            MODE_PRIVATE
        )
        var auth = prefs.getString(context.getString(R.string.access_token), "")
        // exclamation marks is to ignore nullability
        auth = "Bearer $auth"
        CapService().getEvents(auth, context)
        CapService().getReminders(auth, context)
    }

    fun getUserOrganization(context: Context) {
        Log.d(TAG, "get the user organization")

        var organizationResponse: OrganizationResponse

        var prefs = context.getSharedPreferences(
            context.getString(R.string.shared_preferences_key),
            MODE_PRIVATE
        )
        var auth = prefs.getString(context.getString(R.string.access_token), "")
        // exclamation marks is to ignore nullability
        auth = "Bearer $auth"
        Log.d(TAG, "user org call: $auth")

        Retrofit2Client.instance.getUsersOrg(auth)
            .enqueue(object : Callback<OrganizationResponse> {
                override fun onFailure(call: Call<OrganizationResponse>?, t: Throwable) {
                    Log.d(TAG, t.toString())
                    Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(
                    call: Call<OrganizationResponse>?,
                    response: Response<OrganizationResponse>
                ) {
                    Log.d(TAG, "Successful user org call: "+ response)

                    // check to make sure that it was a successful return to server
                    if (response.code() == 200) {
                        // begin generating the alarm notifications from the API response
                        organizationResponse = response.body()!!
                        val editor = context.getSharedPreferences(
                            context.getString(R.string.shared_preferences_key),
                            MODE_PRIVATE
                        ).edit()

                        // store access token
                        editor.putString(
                            context.getString(R.string.org_address),
                            response.body()?.street_address + ", " + response.body()?.city + ", " + response.body()?.province_state
                        ).apply()
                    }
                }
            })

    }

    fun getLocationFromAddress(strAddress: String, context: Context ): GeoPoint? {

        val geocoder = Geocoder(context)
        val addresses:List<Address>
        addresses = geocoder.getFromLocationName(strAddress,1)

        if (addresses.isNotEmpty())
        {
            val latitude = addresses[0].latitude
            val longitude = addresses[0].longitude
            return GeoPoint(
                 latitude ,
                 longitude
             )
        }
        return null
    }
}

private const val TAG = "CapService"

package com.capstone

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
<<<<<<< HEAD:app/src/main/java/com/capstone/MainActivity.kt
=======
import com.capstone.R
import com.google.android.gms.tasks.OnCompleteListener
>>>>>>> added firebase notifications:app/src/main/java/com/capstone/activities/MainActivity.kt
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.iid.FirebaseInstanceId
import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Build
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.ContextCompat
import com.capstone.NotificationReceiver
import com.capstone.api.Retrofit2Client
import com.capstone.displayNotificationLater
import com.capstone.models.DisplayNotification
import com.capstone.models.events.Event
import com.capstone.models.events.EventsList
import com.capstone.sendNotification
import com.google.firebase.FirebaseApp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat.getTimeInstance
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // SET firebase messaging
        FirebaseApp.initializeApp(this);
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token

                // Log and toast
                Log.d(TAG, token)
                Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
            })


        //check log in status
        val sharedPref = this.getSharedPreferences(
            getString(R.string.shared_preferences_key), Context.MODE_PRIVATE
        )
        val isLoggedIn = sharedPref.getBoolean(getString(R.string.is_logged_in_key), false)

        if (!isLoggedIn) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

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


        // create the notification channel
        createChannel()

        // get the events
        getEvents()

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

    private fun getEvents() {
        val sharedPref = this.getSharedPreferences(
            getString(R.string.shared_preferences_key), Context.MODE_PRIVATE
        )
        // exclamation marks is to ignore nullability
        var auth = sharedPref!!.getString(getString(R.string.access_token), "default")!!
        auth = "Bearer $auth"

        Retrofit2Client.instance.getEvents(auth)
            .enqueue(object : Callback<EventsList> {
                override fun onFailure(call: Call<EventsList>?, t: Throwable) {
                    Log.d("LOG_TAG_CHECK", t.toString())
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(
                    call: Call<EventsList>?,
                    response: Response<EventsList>
                ) {
                    // check to make sure that it was a successful return to server
                    if (response.code() == 200) {
                        // begin generating the alarm notifications from the API response
                        generateAlarmNotifications(response?.body()!!)
                    }
                }
            })
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
        displayNotification: DisplayNotification
    ) {
        val notificationManager = getSystemService(
            NotificationManager::class.java
        )

        notificationManager?.displayNotificationLater(
            displayNotification,
            applicationContext
        )
    }

    private fun generateAlarmNotifications(eventsList: EventsList) {

        eventsList.content.forEach { event ->
            val start = event.start_time
            val alarmT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA).parse(start)!!.time
            val timeNow = Calendar.getInstance().timeInMillis
            val diff = alarmT - timeNow

            //create new alarms for future
            if (diff > 0)
            {
                Log.d("LOG_TAG_CHECK", "Creating new event @$start in $diff ms")
                val displayNotification = DisplayNotification(event.id.toInt(), event.title, event.description, alarmT)
                displayNotificationLater(displayNotification)
            }
            Log.d("LOG_TAG_CHECK", "Past Start Time @$start notification not created")

        }
    }
    
}

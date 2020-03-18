package com.capstone.events

import android.content.Context
import android.os.Bundle
import android.service.voice.AlwaysOnHotwordDetector
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.R
import com.capstone.api.Retrofit2Client
import com.capstone.models.events.Event
import com.capstone.models.events.EventPayload
import kotlinx.android.synthetic.main.add_event.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Timestamp

class AddEvent : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_event)
        var btnSubmit = findViewById<Button>(R.id.btn_event_submit)

        btnSubmit.setOnClickListener {
            /*
            val title = editEventTitle.text.toString().trim()
            val description = editEventDescription.text.toString().trim()
            val location = editEventLocation.text.toString().trim()
            val startTime = editEventStartTime.text.toString().trim()
            val endTime = editEventEndTime.text.toString().trim()
            val attendees = editEventAttendee.text.toString().trim()
             */

            val title = "Testing App Event Creation"
            val description = "This is a Test"
            val location = "Test Town"
            val startTime = "2020-01-28T08:00:00"
            val endTime = "2020-01-28T08:00:00"
            val attendees = "john@email.com"
            eventCreateRequest(title, description, location, startTime, endTime, attendees)

        }
    }

    private fun eventCreateRequest(title: String, description: String, location: String,
                                   startTime: String, endTime: String, attendees: String) {
        val sharedPrefs = getSharedPreferences(
            getString(R.string.shared_preferences_key),
            Context.MODE_PRIVATE
        )
        var auth = sharedPrefs!!.getString(getString(R.string.access_token), "default")!!
        auth = "Bearer $auth"
        val attendeeList = attendees.split(",")
        val payload = EventPayload(title, description, location, startTime, endTime,
            attendeeList.toTypedArray()
        )

        Retrofit2Client.instance.createEvent(auth, payload)
            .enqueue(object : Callback<Event> {
                override fun onFailure(call: Call<Event>?, t:Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<Event>, response: Response<Event>) {
                    if (response.code() == 200) {
                        finish()
                        return
                    } else {
                        Toast.makeText(applicationContext, response.code().toString(), Toast.LENGTH_LONG).show()
                    }
                }
            })
    }
}


package com.capstone.ui.events

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.R
import com.capstone.api.Retrofit2Client
import com.capstone.api.response.events.Event
import com.capstone.api.response.events.EventPayload
import kotlinx.android.synthetic.main.edit_event.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class EditEvent : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_event)
        val card = intent.extras?.get("event_object") as Event
        changeEventTitle.append(card.title)
        changeEventDescription.append(card.description)
        changeEventLocation.append(card.location)
        changeEventStartTime.append(card.start_time)
        changeEventEndTime.append(card.end_time)
        var attendeeList = ""
        card.attendees?.forEach { user ->
            attendeeList += user.email
        }

        changeEventAttendee.append(attendeeList)

        val sharedPref = this.getSharedPreferences(
            getString(R.string.shared_preferences_key), Context.MODE_PRIVATE
        )
        // exclamation marks is to ignore nullability
        var auth = sharedPref!!.getString(getString(R.string.access_token), "default")!!
        auth = "Bearer $auth"

        var userEmail = sharedPref!!.getString(getString(R.string.user_email), "default")!!

        btn_event_edit_submit.setOnClickListener {
            if (userEmail == card.creator.email) {
                val eventID = card.id.toString()
                val eventTitle = changeEventTitle.text.trim().toString()
                val eventDescription = changeEventDescription.text.trim().toString()
                val eventLocation = changeEventLocation.text.trim().toString()
                val startTimeArray = changeEventStartTime.text.trim().split(" ").toString()
                val eventStartTime = startTimeArray[0] + "T" + startTimeArray[1]
                val endTimeArray = changeEventEndTime.text.trim().split(" ").toString()
                val eventEndTime = startTimeArray[0] + "T" + startTimeArray[1]
                val attendeeArray = changeEventAttendee.text.trim().split(",")
                val eventAttendees = ""
                attendeeArray.forEach { attendee ->
                    eventAttendees + attendee
                }

                editEventRequest(eventID, eventTitle, eventDescription, eventLocation, eventStartTime,
                    eventEndTime, attendeeArray.toTypedArray(), auth)
            } else {
                Toast.makeText(
                    applicationContext,
                    "You do not have permission to edit this event!",
                    Toast.LENGTH_LONG
                ).show()
            }

        }
    }

    private fun editEventRequest(id: String, title: String, description: String, location: String,
                                 startTime: String, endTime: String, attendees: Array<String>, auth: String) {
        val titleTest = "Testing App Event Creation"
        val descriptionTest = "This is a Test"
        val locationTest = "Test Town"
        val startTimeTest = "2020-01-28T08:00:00"
        val endTimeTest = "2020-01-28T09:00:00"
        val attendeesTest = ("john@email.com").split(",").toTypedArray()
        val payload = EventPayload(titleTest, descriptionTest, locationTest, startTimeTest, endTimeTest, attendeesTest)

        Retrofit2Client.instance.editEvent(auth, id, payload)
            .enqueue(object : Callback<Event> {
                override fun onFailure(call: Call<Event>?, t:Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<Event>, response: Response<Event>) {
                    if (response.code() == 200) {
                        val resultIntent = Intent()
                        val newEvent = response.body() as Event
                        resultIntent.putExtra("return_event", newEvent as Serializable)
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                        return
                    } else {
                        Toast.makeText(applicationContext, response.body().toString(), Toast.LENGTH_LONG).show()
                    }
                }
            })
    }
}
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
import com.capstone.ui.EditTime
import kotlinx.android.synthetic.main.edit_event.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable
import java.text.SimpleDateFormat

class EditEvent : AppCompatActivity() {
    private val REQUEST_FORM1 = 1
    private val REQUEST_FORM2 = 2

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
        var counter = 0
        card.attendees?.forEach { user ->
            if (counter != 0) {
                attendeeList += ","
            }

            counter++
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

        changeEventStartTime.setOnClickListener {
            val intent = Intent(applicationContext, EditTime::class.java)
            intent.putExtra("time_object", card.start_time as Serializable)
            startActivityForResult(intent, REQUEST_FORM1)
        }

        changeEventEndTime.setOnClickListener {
            val intent = Intent(applicationContext, EditTime::class.java)
            intent.putExtra("time_object", card.end_time as Serializable)
            startActivityForResult(intent, REQUEST_FORM2)
        }

        btn_event_edit_submit.setOnClickListener {
            if (userEmail == card.creator.email) {
                val eventID = card.id.toString()
                val eventTitle = changeEventTitle.text.trim().toString()
                val eventDescription = changeEventDescription.text.trim().toString()
                val eventLocation = changeEventLocation.text.trim().toString()
                val startTime = changeEventStartTime.text.trim().toString()
                val endTime = changeEventEndTime.text.trim().toString()
                val attendeeArray = changeEventAttendee.text.trim().split(",")

                editEventRequest(eventID, eventTitle, eventDescription, eventLocation, startTime,
                    endTime, attendeeArray.toTypedArray(), auth)
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
        val dateFormatter =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val dateStart = dateFormatter.parse(startTime)
        val dateEnd = dateFormatter.parse(endTime)
        val returnDateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val newStartTime = returnDateFormatter.format(dateStart)
        val newEndTime = returnDateFormatter.format(dateEnd)
        val payload = EventPayload(title, description, location, newStartTime, newEndTime, attendees)

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_FORM1 && resultCode == Activity.RESULT_OK) {
            val date = data?.getSerializableExtra("time_object") as String
            changeEventStartTime.setText(date)
        } else if (requestCode == REQUEST_FORM2 && resultCode == Activity.RESULT_OK) {
            val date = data?.getSerializableExtra("time_object") as String
            changeEventEndTime.setText(date)
        }
    }
}
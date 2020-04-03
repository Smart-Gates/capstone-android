package com.capstone.ui.events

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.R
import com.capstone.api.Retrofit2Client
import com.capstone.api.response.events.Event
import com.capstone.api.response.events.EventPayload
import com.capstone.ui.EditTime
import kotlinx.android.synthetic.main.add_event.*
import kotlinx.android.synthetic.main.edit_event.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable
import java.text.SimpleDateFormat

class AddEvent : AppCompatActivity(){
    private val REQUEST_FORM1 = 1
    private val REQUEST_FORM2 = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_event)
        var btnSubmit = findViewById<Button>(R.id.btn_event_submit)

        btnSubmit.setOnClickListener {

            val title = editEventTitle.text.toString().trim()
            val description = editEventDescription.text.toString().trim()
            val location = editEventLocation.text.toString().trim()
            val startTime = editEventStartTime.text.toString().trim()
            val endTime = editEventEndTime.text.toString().trim()
            val attendees = editEventAttendee.text.toString().trim().split(",").toTypedArray()

            eventCreateRequest(title, description, location, startTime, endTime, attendees)

        }

        editEventStartTime.setOnClickListener {
            val intent = Intent(applicationContext, EditTime::class.java)
            intent.putExtra("time_object", editEventStartTime.text.toString() as Serializable)
            startActivityForResult(intent, REQUEST_FORM1)
        }

        editEventEndTime.setOnClickListener {
            val intent = Intent(applicationContext, EditTime::class.java)
            intent.putExtra("time_object", editEventStartTime.text.toString() as Serializable)
            startActivityForResult(intent, REQUEST_FORM2)
        }
    }

    private fun eventCreateRequest(title: String, description: String, location: String,
                                   startTime: String, endTime: String, attendees: Array<String>) {
        val sharedPrefs = getSharedPreferences(
            getString(R.string.shared_preferences_key),
            Context.MODE_PRIVATE
        )
        var auth = sharedPrefs!!.getString(getString(R.string.access_token), "default")!!
        auth = "Bearer $auth"
        val dateFormatter =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val dateStart = dateFormatter.parse(startTime)
        val dateEnd = dateFormatter.parse(endTime)
        val returnDateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val newStartTime = returnDateFormatter.format(dateStart)
        val newEndTime = returnDateFormatter.format(dateEnd)
        val payload = EventPayload(title, description, location, newStartTime, newEndTime, attendees)

        Retrofit2Client.instance.createEvent(auth, payload)
            .enqueue(object : Callback<Event> {
                override fun onFailure(call: Call<Event>?, t:Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<Event>, response: Response<Event>) {
                    if (response.code() == 201) {
                        finish()
                        return
                    } else {
                        Toast.makeText(applicationContext, response.code().toString(), Toast.LENGTH_LONG).show()
                    }
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_FORM1 && resultCode == Activity.RESULT_OK) {
            val date = data?.getSerializableExtra("time_object") as String
            editEventStartTime.setText(date)
        } else if (requestCode == REQUEST_FORM2 && resultCode == Activity.RESULT_OK) {
            val date = data?.getSerializableExtra("time_object") as String
            editEventEndTime.setText(date)
        }
    }
}


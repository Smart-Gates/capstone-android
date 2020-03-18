package com.capstone.reminders
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.R
import com.capstone.api.Retrofit2Client
import com.capstone.models.reminders.Reminder
import com.capstone.models.reminders.ReminderPayload
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddReminder : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_reminder)
        var btnSubmit = findViewById<Button>(R.id.btn_reminder_submit)

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
            val startTime = "2020-01-28T08:00:00"
            reminderCreateRequest(title, description, startTime)

        }
    }

    private fun reminderCreateRequest(title: String, description: String, startTime: String) {
        val sharedPrefs = getSharedPreferences(
            getString(R.string.shared_preferences_key),
            Context.MODE_PRIVATE
        )
        var auth = sharedPrefs!!.getString(getString(R.string.access_token), "default")!!
        auth = "Bearer $auth"
        val payload = ReminderPayload(title, description,  startTime
        )

        Retrofit2Client.instance.createReminder(auth, payload)
            .enqueue(object : Callback<Reminder> {
                override fun onFailure(call: Call<Reminder>?, t:Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<Reminder>, response: Response<Reminder>) {
                    if (response.code() == 201) {
                        finish()
                        return
                    } else {
                        Toast.makeText(applicationContext, response.code().toString(), Toast.LENGTH_LONG).show()
                    }
                }
            })
    }
}


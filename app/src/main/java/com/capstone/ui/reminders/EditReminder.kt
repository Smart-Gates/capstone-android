package com.capstone.ui.reminders

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.R
import com.capstone.api.Retrofit2Client
import com.capstone.api.response.reminders.Reminder
import com.capstone.api.response.reminders.ReminderPayload
import com.capstone.ui.EditTime
import kotlinx.android.synthetic.main.add_event.*
import kotlinx.android.synthetic.main.add_reminder.*
import kotlinx.android.synthetic.main.edit_reminder.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable
import java.text.SimpleDateFormat

class EditReminder : AppCompatActivity() {
    private val REQUEST_FORM1 = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_reminder)
        val card = intent.extras?.get("reminder_object") as Reminder
        changeReminderTitle.append(card.title)
        changeReminderDescription.append(card.description)
        changeReminderStartTime.append(card.start_time)

        val sharedPref = this.getSharedPreferences(
            getString(R.string.shared_preferences_key), Context.MODE_PRIVATE
        )
        // exclamation marks is to ignore nullability
        var auth = sharedPref!!.getString(getString(R.string.access_token), "default")!!
        auth = "Bearer $auth"

        var userEmail = sharedPref!!.getString(getString(R.string.user_email), "default")!!

        btn_reminder_edit_submit.setOnClickListener {
            if (userEmail == card.creator.email) {
                val reminderID = card.id.toString()
                val reminderTitle = changeReminderTitle.text.trim().toString()
                val reminderDescription = changeReminderDescription.text.trim().toString()
                val startTime = changeReminderStartTime.text.trim().toString()

                editReminderRequest(reminderID, reminderTitle, reminderDescription,  startTime, auth)
            } else {
                Toast.makeText(
                    applicationContext,
                    "You do not have permission to edit this event!",
                    Toast.LENGTH_LONG
                ).show()
            }

        }

        changeReminderStartTime.setOnClickListener {
            val intent = Intent(applicationContext, EditTime::class.java)
            intent.putExtra("time_object", changeReminderStartTime.text.toString() as Serializable)
            startActivityForResult(intent, REQUEST_FORM1)
        }
    }

    private fun editReminderRequest(id: String, title: String, description: String,
                                    startTime: String, auth: String) {
        val dateFormatter =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val dateStart = dateFormatter.parse(startTime)
        val returnDateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val newStartTime = returnDateFormatter.format(dateStart)
        val payload = ReminderPayload(title, description, newStartTime)

        Retrofit2Client.instance.editReminder(auth, id, payload)
            .enqueue(object : Callback<Reminder> {
                override fun onFailure(call: Call<Reminder>?, t:Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<Reminder>, response: Response<Reminder>) {
                    if (response.code() == 200) {
                        val resultIntent = Intent()
                        val newReminder = response.body() as Reminder
                        resultIntent.putExtra("return_reminder", newReminder as Serializable)
                        setResult(Activity.RESULT_OK, resultIntent)
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
            changeReminderStartTime.setText(date)
        }
    }
}
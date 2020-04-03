package com.capstone.ui.reminders
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.R
import com.capstone.api.Retrofit2Client
import com.capstone.api.response.reminders.Reminder
import com.capstone.api.response.reminders.ReminderPayload
import com.capstone.ui.EditTime
import kotlinx.android.synthetic.main.add_reminder.*
import kotlinx.android.synthetic.main.edit_reminder.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable
import java.text.SimpleDateFormat

class AddReminder : AppCompatActivity(){
    private val REQUEST_FORM1 = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_reminder)
        var btnSubmit = findViewById<Button>(R.id.btn_reminder_submit)

        btnSubmit.setOnClickListener {

            val title = editReminderTitle.text.trim().toString()
            val description = editReminderDescription.text.trim().toString()
            val startTime = editReminderStartTime.text.trim().toString()

            reminderCreateRequest(title, description, startTime)

        }

        editReminderStartTime.setOnClickListener {
            val intent = Intent(applicationContext, EditTime::class.java)
            intent.putExtra("time_object", editReminderStartTime.text.toString() as Serializable)
            startActivityForResult(intent, REQUEST_FORM1)
        }
    }

    private fun reminderCreateRequest(title: String, description: String, startTime: String) {
        val sharedPrefs = getSharedPreferences(
            getString(R.string.shared_preferences_key),
            Context.MODE_PRIVATE
        )
        var auth = sharedPrefs!!.getString(getString(R.string.access_token), "default")!!
        auth = "Bearer $auth"
        val dateFormatter =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val dateStart = dateFormatter.parse(startTime)
        val returnDateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val newStartTime = returnDateFormatter.format(dateStart)
        val payload = ReminderPayload(title, description, newStartTime)

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_FORM1 && resultCode == Activity.RESULT_OK) {
            val date = data?.getSerializableExtra("time_object") as String
            editReminderStartTime.setText(date)
        }
    }
}


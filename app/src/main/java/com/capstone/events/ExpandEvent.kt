package com.capstone.events

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.R
import com.capstone.api.Retrofit2Client
import com.capstone.models.events.Event
import kotlinx.android.synthetic.main.expand_event.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class ExpandEvent : AppCompatActivity() {
    private val REQUEST_FORM = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.expand_event)
        val card = intent.extras?.get("event_object") as Event
        updateEditText(card.title, card.description, card.location, card.start_time, card.end_time, card.attendees.toString())

        val sharedPref = this.getSharedPreferences(
            getString(R.string.shared_preferences_key), Context.MODE_PRIVATE
        )
        // exclamation marks is to ignore nullability
        var auth = sharedPref!!.getString(getString(R.string.access_token), "default")!!
        auth = "Bearer $auth"

        var userEmail = sharedPref!!.getString(getString(R.string.user_email), "default")!!

        event_delete_btn.setOnClickListener {
            if (userEmail == card.creator.email) {
                deleteEvent(auth, card)
            } else {
                Toast.makeText(
                    applicationContext,
                    "You do not have permission to delete this event!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        event_edit_btn.setOnClickListener {
            val intent = Intent(applicationContext, EditEvent::class.java)
            intent.putExtra("event_object", card as Serializable)
            startActivityForResult(intent, REQUEST_FORM)
        }
    }

    private fun deleteEvent (auth: String, card: Event) {
        Retrofit2Client.instance.deleteEvent(auth, card.id.toString()).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(
                    applicationContext,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.code() == 200) {
                    //Toast.makeText(applicationContext, response.body().toString(), Toast.LENGTH_LONG).show()
                    finish()
                    return
                } else {
                    Toast.makeText(applicationContext, response.code(), Toast.LENGTH_LONG).show()
                }
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // check if the requestCode is the wanted one and if the result is what we are expecting
        if (requestCode == REQUEST_FORM && resultCode == RESULT_OK) {
            val name = data?.getSerializableExtra("return_event") as Event
            updateEditText(name.title, name.description, name.location, name.start_time, name.end_time, name.attendees.toString())
        }
    }

    private fun updateEditText( title: String, description: String, location: String,
                                startTime: String, endTime: String, attendees: String) {
        viewEventTitle.text = title
        viewEventDescription.text = description
        viewEventLocation.text = location
        viewEventStartTime.text = startTime
        viewEventEndTime.text = endTime

    }
}

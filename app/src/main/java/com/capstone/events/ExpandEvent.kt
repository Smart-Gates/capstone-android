package com.capstone.events

import android.content.Context
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

class ExpandEvent () : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.expand_event)
        val card = intent.extras?.get("event_object") as Event
        viewEventTitle.text = card.title
        viewEventDescription.text = card.description
        viewEventLocation.text = card.location
        viewEventStartTime.text = card.start_time
        viewEventEndTime.text = card.end_time

        val sharedPref = this.getSharedPreferences(
            getString(R.string.shared_preferences_key), Context.MODE_PRIVATE
        )
        // exclamation marks is to ignore nullability
        var auth = sharedPref!!.getString(getString(R.string.access_token), "default")!!
        auth = "Bearer $auth"

        var email = sharedPref!!.getString(getString(R.string.user_email), "default")!!
        email ="$email"
        Toast.makeText(applicationContext, email, Toast.LENGTH_LONG).show()

        event_delete_btn.setOnClickListener {



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
                    Toast.makeText(applicationContext, response.body().toString(), Toast.LENGTH_LONG).show()
                    finish()
                    return
                } else {
                    Toast.makeText(applicationContext, response.code(), Toast.LENGTH_LONG).show()
                }
            }

        })
    }
}

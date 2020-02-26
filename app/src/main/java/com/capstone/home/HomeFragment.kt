package com.capstone.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import android.content.SharedPreferences
import com.capstone.R

import com.capstone.api.Retrofit2Client
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import com.capstone.events.EventViewModel
import com.capstone.reminders.ReminderViewModel

import com.capstone.models.events.EventsList
import kotlinx.android.synthetic.main.fragment_home.view.*
import com.capstone.activities.subviews.WeatherActivity
import com.capstone.models.events.Event
import com.capstone.models.events.EventResponse


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var reminderViewModel: ReminderViewModel
    private lateinit var eventViewModel: EventViewModel
    private lateinit var root : View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_home, container, false)
        initViewModels()

        root.btn_weather.setOnClickListener {
            eventRequest()
        }


        return root
    }

    private fun initViewModels () {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        reminderViewModel = ViewModelProviders.of(this).get(ReminderViewModel::class.java)
        eventViewModel = ViewModelProviders.of(this).get(EventViewModel::class.java)
    }

    private fun eventRequest () {
        val sharedPrefs =
            this.activity!!.getSharedPreferences(getString(R.string.shared_preferences_key),
                Context.MODE_PRIVATE)
        var auth = sharedPrefs!!.getString(getString(R.string.access_token), "default")!!
        auth = "Bearer $auth"

        Retrofit2Client.instance.getEvents(auth)
            .enqueue(object : Callback<EventResponse> {
                override fun onFailure(
                    call: Call<EventResponse>,
                    t: Throwable
                ) {
                    Toast.makeText(activity, t.message, Toast.LENGTH_LONG).show()

                }

                override fun onResponse(
                    call: Call<EventResponse>,
                    response: Response<EventResponse>
                ) {
                    if (response.code() == 200) {
                        val events : List<Event>? = response.body()?.eventList

                        if (events != null) {
                            Toast.makeText(activity, events.size, Toast.LENGTH_LONG).show()
                        }
                        //testEvent()
                    }
                }
        })
    }

    private fun testEvent () {

        val stamp : String = eventViewModel.getEvent(0)?.start_time.toString()
        val stampSplit = stamp.split(" ")
        val date = stampSplit[0]
        val time = stampSplit[1]
        val dateSplit = date.split("-")
        val timeSplit = time.split(":")
        val month = dateSplit[1]

        root.events_month_m1.text = month
    }
}
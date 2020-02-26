package com.capstone.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
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
        eventRequest()

        root.btn_weather.setOnClickListener {
            val intent = Intent(activity, WeatherActivity::class.java)
            startActivity(intent)
        }


        return root
    }

    private fun initViewModels () {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        reminderViewModel = ViewModelProviders.of(this).get(ReminderViewModel::class.java)
        eventViewModel = ViewModelProviders.of(this).get(EventViewModel::class.java)
    }

    private fun eventRequest () {
        Retrofit2Client.instance.getEvents(getString(R.string.access_token)).enqueue(object : Callback<EventsList> {
            override fun onFailure(call: Call<EventsList>, t: Throwable) {
                Toast.makeText(activity, t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<EventsList>?, response: Response<EventsList>) {
                if (response.code() == 200) {
                    eventViewModel.setEvents(response.body()?.content)
                    root.meeting_title_m1.text = "RESPONDED"
                    testEvent()
                    return
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
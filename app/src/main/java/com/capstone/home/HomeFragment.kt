package com.capstone.home

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.capstone.R
import com.capstone.activities.subviews.WeatherActivity
import com.capstone.api.Retrofit2Client
import com.capstone.events.AddEvent
import com.capstone.events.EventViewModel
import com.capstone.models.events.EventsList
import com.capstone.models.reminders.RemindersList
import com.capstone.reminders.ReminderViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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
        reminderRequest()

        root.btn_weather.setOnClickListener {
            val intent = Intent(activity, WeatherActivity::class.java)
            startActivity(intent)
        }

        root.btn_event.setOnClickListener {
            val intent = Intent(activity, AddEvent::class.java)
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
        val sharedPrefs =
            this.activity!!.getSharedPreferences(getString(R.string.shared_preferences_key),
                Context.MODE_PRIVATE)
        var auth = sharedPrefs!!.getString(getString(R.string.access_token), "default")!!
        auth = "Bearer $auth"

        Retrofit2Client.instance.getEvents(auth)
            .enqueue(object : Callback<EventList> {
                override fun onFailure(
                    call: Call<EventList>,
                    t: Throwable
                ) {
                    Toast.makeText(activity, t.message, Toast.LENGTH_LONG).show()

                }

                override fun onResponse(
                    call: Call<EventList>,
                    response: Response<EventList>
                ) {
                    if (response.code() == 200) {
                        eventViewModel.setEvents(response.body())
                        populateEvent()
                    }
                }
        })
    }

    private fun reminderRequest () {
        val sharedPrefs =
            this.activity!!.getSharedPreferences(getString(R.string.shared_preferences_key),
                Context.MODE_PRIVATE)
        var auth = sharedPrefs!!.getString(getString(R.string.access_token), "default")!!
        auth = "Bearer $auth"

        Retrofit2Client.instance.getReminders(auth)
            .enqueue(object : Callback<RemindersList> {
                override fun onFailure(
                    call: Call<RemindersList>,
                    t: Throwable
                ) {
                    Toast.makeText(activity, t.message, Toast.LENGTH_LONG).show()

                }

                override fun onResponse(
                    call: Call<RemindersList>,
                    response: Response<RemindersList>
                ) {
                    if (response.code() == 200) {
                        reminderViewModel.setReminders(response.body())
                        populateReminder()
                    }
                }
            })
    }

    private fun populateEvent () {
        var pastEvent = RelativeLayout(activity)
        var counter = 0

        eventViewModel.getEventsList()?.content?.forEach { event ->

            val card = RelativeLayout(activity)
            card.id = counter

            var params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 120)
            params.setMargins(15, 30, 15, 0)

            if (counter != 0) {
                params.addRule(RelativeLayout.BELOW, pastEvent.id)
            }

            card.layoutParams = params
            card.setBackgroundColor(Color.WHITE)

            var titleLayout = RelativeLayout(activity)
            params = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(10, 0, 0, 5)
            titleLayout.layoutParams = params
            var title = TextView(activity)
            title.setTextColor(Color.BLACK)
            title.textSize = 18F
            title.typeface = Typeface.DEFAULT_BOLD
            title.id = View.generateViewId()
            title.text = event.title
            titleLayout.addView(title)

            var descriptionLayout = RelativeLayout(activity)
            params = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            params.addRule(RelativeLayout.BELOW, title.id)
            params.setMargins(10, 50, 10, 0)
            descriptionLayout.layoutParams = params
            var description = TextView(activity)
            description.setTextColor(Color.BLACK)
            description.text = event.description
            descriptionLayout.addView(description)

            card.addView(titleLayout)
            card.addView(descriptionLayout)

            counter++
            pastEvent = card
            root.event_group.addView(card)


        }

    }

    private fun populateReminder () {
        var pastReminder = RelativeLayout(activity)
        var counter = 0

        reminderViewModel.getRemindersList()?.content?.forEach { reminder ->

            // Create and Display Reminder Card
            val card = RelativeLayout(activity)
            card.id = View.generateViewId()
            var params =
                RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            params.setMargins(15, 30, 15, 0)
            if (counter != 0) {
                params.addRule(RelativeLayout.BELOW, pastReminder.id)
            }
            card.layoutParams = params
            card.setBackgroundColor(Color.WHITE)

            // Add Reminder Title to Reminder Card
            var titleLayout = RelativeLayout(activity)
            titleLayout.id = View.generateViewId()
            params = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(10, 0, 0, 5)
            titleLayout.layoutParams = params
            var title = TextView(activity)
            title.setTextColor(Color.BLACK)
            title.textSize = 18F
            title.typeface = Typeface.DEFAULT_BOLD
            title.id = View.generateViewId()
            title.text = reminder.title
            titleLayout.addView(title)

            // Add Reminder Description to Reminder Card
            var descriptionLayout = RelativeLayout(activity)
            descriptionLayout.id = View.generateViewId()
            params = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            params.addRule(RelativeLayout.BELOW, titleLayout.id)
            params.setMargins(10, 0, 10, 0)
            descriptionLayout.layoutParams = params
            var description = TextView(activity)
            description.setTextColor(Color.BLACK)
            description.text = reminder.description
            descriptionLayout.addView(description)

            // Add Reminder Time to Reminder Card
            var timeLayout = RelativeLayout(activity)
            params = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            params.addRule(RelativeLayout.BELOW, descriptionLayout.id)
            params.setMargins(10, 0, 0, 0)
            timeLayout.layoutParams = params
            var time = TextView(activity)
            time.setTextColor(Color.BLACK)
            time.text = reminder.start_time
            timeLayout.addView(time)

            // Add all the TextView to the Card
            card.addView(titleLayout)
            card.addView(descriptionLayout)
            card.addView(timeLayout)

            counter++
            pastReminder = card
            root.reminders_group.addView(card)
        }
    }
}

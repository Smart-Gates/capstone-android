package com.capstone.ui.home

import Weather
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.capstone.R
import com.capstone.api.Retrofit2Client
import com.capstone.api.response.OrganizationResponse
import com.capstone.api.response.events.EventList
import com.capstone.api.response.reminders.ReminderList
import com.capstone.api.response.weather.WeatherViewModel
import com.capstone.ui.events.AddEvent
import com.capstone.ui.events.EventViewModel
import com.capstone.ui.events.ExpandEvent
import com.capstone.ui.organization.OrganizationViewModel
import com.capstone.ui.reminders.AddReminder
import com.capstone.ui.reminders.ExpandReminder
import com.capstone.ui.reminders.ReminderViewModel
import com.capstone.ui.weather.WeatherActivity
import com.capstone.utils.CapService
import com.capstone.utils.location.LocationProvider
import kotlinx.android.synthetic.main.fragment_home.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable
import java.text.SimpleDateFormat


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var reminderViewModel: ReminderViewModel
    private lateinit var eventViewModel: EventViewModel
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var organizationViewModel: OrganizationViewModel
    private lateinit var root: View

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_home, container, false)
        initViewModels()
        eventRequest()
        reminderRequest()
        weatherRequest()
        organizationRequest()

        root.btn_weather.setOnClickListener {
            val intent = Intent(activity, WeatherActivity::class.java)
            startActivity(intent)
        }

        root.btn_event_add.setOnClickListener {
            val intent = Intent(activity, AddEvent::class.java)
            startActivity(intent)
        }

        root.btn_reminder_add.setOnClickListener {
            val intent = Intent(activity, AddReminder::class.java)
            startActivity(intent)
        }

        return root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onResume() {
        super.onResume()
        eventRequest()
        reminderRequest()
        weatherRequest()
    }

    private fun initViewModels() {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        reminderViewModel = ViewModelProviders.of(this).get(ReminderViewModel::class.java)
        eventViewModel = ViewModelProviders.of(this).get(EventViewModel::class.java)
        weatherViewModel = ViewModelProviders.of(this).get(WeatherViewModel::class.java)
        organizationViewModel = ViewModelProviders.of(this).get(OrganizationViewModel::class.java)
    }

    private fun eventRequest() {
        val sharedPrefs =
            this.activity!!.getSharedPreferences(
                getString(R.string.shared_preferences_key),
                Context.MODE_PRIVATE
            )
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

    private fun reminderRequest() {

        val sharedPrefs =
            this.activity!!.getSharedPreferences(
                getString(R.string.shared_preferences_key),
                Context.MODE_PRIVATE
            )
        var auth = sharedPrefs!!.getString(getString(R.string.access_token), "default")!!
        auth = "Bearer $auth"

        Retrofit2Client.instance.getReminders(auth)
            .enqueue(object : Callback<ReminderList> {
                override fun onFailure(
                    call: Call<ReminderList>,
                    t: Throwable
                ) {
                    Toast.makeText(activity, t.message, Toast.LENGTH_LONG).show()

                }

                override fun onResponse(
                    call: Call<ReminderList>,
                    response: Response<ReminderList>
                ) {
                    if (response.code() == 200) {
                        reminderViewModel.setReminders(response.body())
                        populateReminder()
                    }
                }
            })
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun weatherRequest() {
        CapService().checkPermissionsLocation(this.activity!!, context!!)
        val sharedPrefs =
            this.activity!!.getSharedPreferences(
                getString(R.string.shared_preferences_key),
                Context.MODE_PRIVATE
            )
        var auth = sharedPrefs!!.getString(getString(R.string.access_token), "default")!!
        auth = "Bearer $auth"

        // using the location provider to get the long and lat, create the listener to set the viewModel
        LocationProvider.instance!!.init(context)
        LocationProvider.instance!!.getLocation()?.addOnSuccessListener { location: Location? ->
            Retrofit2Client.instance.getWeather(
                auth, location!!.longitude.toString(), location.latitude.toString()
            ).enqueue(object : Callback<Weather> {
                override fun onFailure(
                    call: Call<Weather>,
                    t: Throwable
                ) {
                    Log.d(TAG, "Unsuccessful weather call")
                }

                override fun onResponse(
                    call: Call<Weather>,
                    response: Response<Weather>
                ) {
                    if (response.code() == 200) {
                        weatherViewModel.setWeather(response.body())
                        Log.d(TAG, "Successful weather call")
                        populateWeather()
                    }
                }
            })
        }
    }

    private fun organizationRequest() {
        val sharedPrefs =
            this.activity!!.getSharedPreferences(
                getString(R.string.shared_preferences_key),
                Context.MODE_PRIVATE
            )
        var auth = sharedPrefs!!.getString(getString(R.string.access_token), "")!!
        auth = "Bearer $auth"

        Retrofit2Client.instance.getUsersOrg(auth)
            .enqueue(object : Callback<OrganizationResponse> {
                override fun onFailure(call: Call<OrganizationResponse>?, t: Throwable) {
                    Log.d(TAG, t.toString())
                    Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(
                    call: Call<OrganizationResponse>?,
                    response: Response<OrganizationResponse>
                ) {
                    Log.d(TAG, "Successful user org call: $response")

                    // check to make sure that it was a successful return to server
                    if (response.code() == 200) {
                        // begin generating the alarm notifications from the API response
                        organizationViewModel.setorganization(response.body())
                        populateOrganization()
                    }
                }
            })
    }

    @SuppressLint("SimpleDateFormat")
    private fun populateEvent() {
        var pastEvent = RelativeLayout(activity)
        var counter = 0
        root.event_group.removeAllViews()

        eventViewModel.getEventsList()?.content?.forEach { event ->
            val eventCard = LinearLayout(activity)
            eventCard.orientation = LinearLayout.VERTICAL
            eventCard.id = counter
            var params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(15, 30, 15, 0)
            eventCard.layoutParams = params
            eventCard.setBackgroundColor(Color.WHITE)

            // Creating the Columns for the Text
            var llColumn = LinearLayout(activity)
            var colParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            llColumn.orientation = LinearLayout.HORIZONTAL

            var leftColumn = LinearLayout(activity)
            var rightColumn = LinearLayout(activity)
            val llColumnParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            llColumnParams.setMargins(10, 0, 10, 0)
            leftColumn.layoutParams = llColumnParams
            rightColumn.layoutParams = llColumnParams
            leftColumn.orientation = LinearLayout.VERTICAL
            rightColumn.orientation = LinearLayout.VERTICAL

            llColumn.addView(leftColumn)
            llColumn.addView(rightColumn)

            // Creating Title TextView
            var title = TextView(activity)
            title.setTextColor(Color.BLACK)
            title.textSize = 18F
            title.typeface = Typeface.DEFAULT_BOLD
            title.text = event.title

            // Creating Description TextView
            var description = TextView(activity)
            description.setTextColor(Color.BLACK)
            description.text = event.description

            // Creating Location TextView
            var location = TextView(activity)
            location.setTextColor(Color.BLACK)
            location.text = event.location

            // Creating Time TextView
            var startTime = event.start_time
            var endTime = event.end_time
            val dateFormatter =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val dateStart = dateFormatter.parse(startTime)
            val dateEnd = dateFormatter.parse(endTime)
            val timeFormatter = SimpleDateFormat("h:mm a")
            startTime = timeFormatter.format(dateStart)
            endTime = timeFormatter.format(dateEnd)
            var time = TextView(activity)
            time.setTextColor(Color.BLACK)
            time.text = "$startTime to $endTime"

            // Creating Day TextView
            var day = TextView(activity)
            var dayFormatter = SimpleDateFormat("d")
            day.text = dayFormatter.format(dateStart)
            day.setTextColor(Color.BLACK)
            day.typeface = Typeface.DEFAULT_BOLD
            day.textSize = 36F
            day.gravity = Gravity.CENTER

            // Creating Month TextView
            val month = TextView(activity)
            dayFormatter = SimpleDateFormat("MMM")
            month.text = dayFormatter.format(dateStart).toUpperCase()
            month.setTextColor(Color.BLACK)
            month.gravity = Gravity.CENTER

            // Adding TextViews to the Appropriate Column
            rightColumn.addView(title)
            rightColumn.addView(description)
            rightColumn.addView(location)
            rightColumn.addView(time)
            leftColumn.addView(day)
            leftColumn.addView(month)

            // Adding the Remaining Views
            eventCard.addView(llColumn)

            // Set Expanded View
            eventCard.setOnClickListener {
                val intent = Intent(activity, ExpandEvent::class.java)
                intent.putExtra("event_object", event as Serializable)
                startActivity(intent)
                populateEvent()
            }

            // Add Card to Group
            root.event_group.addView(eventCard)
        }
    }

    private fun populateReminder() {
        var pastReminder = RelativeLayout(activity)
        var counter = 0
        root.reminders_group.removeAllViews()

        reminderViewModel.getRemindersList()?.content?.forEach { reminder ->
            val reminderCard = LinearLayout(activity)
            reminderCard.orientation = LinearLayout.VERTICAL
            reminderCard.id = counter
            var params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(15, 30, 15, 0)
            reminderCard.layoutParams = params
            reminderCard.setBackgroundColor(Color.WHITE)

            // Creating the Columns for the Text
            var llColumn = LinearLayout(activity)
            var colParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            llColumn.orientation = LinearLayout.HORIZONTAL

            var leftColumn = LinearLayout(activity)
            var rightColumn = LinearLayout(activity)
            val llColumnParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            llColumnParams.setMargins(10, 0, 10, 0)
            leftColumn.layoutParams = llColumnParams
            rightColumn.layoutParams = llColumnParams
            leftColumn.orientation = LinearLayout.VERTICAL
            rightColumn.orientation = LinearLayout.VERTICAL

            llColumn.addView(leftColumn)
            llColumn.addView(rightColumn)

            // Creating Title TextView
            var title = TextView(activity)
            title.setTextColor(Color.BLACK)
            title.textSize = 18F
            title.typeface = Typeface.DEFAULT_BOLD
            title.text = reminder.title

            // Creating Description TextView
            var description = TextView(activity)
            description.setTextColor(Color.BLACK)
            description.text = reminder.description

            // Creating Time TextView
            var startTime = reminder.start_time
            val dateFormatter =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val dateStart = dateFormatter.parse(startTime)
            val timeFormatter = SimpleDateFormat("h:mm a")
            startTime = timeFormatter.format(dateStart)
            var time = TextView(activity)
            time.setTextColor(Color.BLACK)
            time.text = startTime

            // Creating Day TextView
            var day = TextView(activity)
            var dayFormatter = SimpleDateFormat("d")
            day.text = dayFormatter.format(dateStart)
            day.setTextColor(Color.BLACK)
            day.typeface = Typeface.DEFAULT_BOLD
            day.textSize = 36F
            day.gravity = Gravity.CENTER

            // Creating Month TextView
            val month = TextView(activity)
            dayFormatter = SimpleDateFormat("MMM")
            month.text = dayFormatter.format(dateStart).toUpperCase()
            month.setTextColor(Color.BLACK)
            month.gravity = Gravity.CENTER

            // Adding TextViews to the Appropriate Column
            rightColumn.addView(title)
            rightColumn.addView(description)
            rightColumn.addView(time)
            leftColumn.addView(day)
            leftColumn.addView(month)

            // Adding the Remaining Views
            reminderCard.addView(llColumn)

            // Set Expanded View
            reminderCard.setOnClickListener {
                val intent = Intent(activity, ExpandReminder::class.java)
                intent.putExtra("reminder_object", reminder as Serializable)
                startActivity(intent)
                populateEvent()
            }

            // Add Card to Group
            root.reminders_group.addView(reminderCard)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun populateWeather() {
        val currentWeather = weatherViewModel.getWeather()?.currently
        root.weather_conditions.text = currentWeather?.summary
        root.weather_temperature.text = currentWeather?.temperature?.toInt().toString() + "°C"
        root.weather_humidity.text = "Humidity " + currentWeather?.humidity.toString()
        if (currentWeather?.precipType != null) {
            root.weather_precip.text = currentWeather.precipType.toString().toUpperCase()
        } else {
            root.weather_precip.text = ""
        }

        if (currentWeather?.icon != null) {
            when (currentWeather.icon) {
                "clear-day" -> root.weather_icon.setImageResource(R.drawable.sun_96)
                "clear-night" -> root.weather_icon.setImageResource(R.drawable.moon_96)
                "rain" -> root.weather_icon.setImageResource(R.drawable.rain_cloud_96)
                "snow" -> root.weather_icon.setImageResource(R.drawable.cloud_image)
                "sleet" -> root.weather_icon.setImageResource(R.drawable.sleet_96)
                "wind" -> root.weather_icon.setImageResource(R.drawable.wind)
                "fog" -> root.weather_icon.setImageResource(R.drawable.fog_96)
                "cloudy" -> root.weather_icon.setImageResource(R.drawable.clouds_96)
                "partly-cloudy-day" -> root.weather_icon.setImageResource(R.drawable.clouds_96)
                "partly-cloudy-night" -> root.weather_icon.setImageResource(R.drawable.moon_cloud_96)
                else -> {
                    root.weather_icon.setImageResource(R.drawable.cloud_image)
                }
            }
        }
    }

    private fun populateOrganization() {
        val newOrganization = organizationViewModel.getorganization()
        root.organisation_name.text = newOrganization?.name
        root.organisation_city.text = newOrganization?.city + ","
        root.organisation_street.text = newOrganization?.street_address + ","
        root.organisation_province.text = newOrganization?.province_state + ","
        root.organisation_zip.text = newOrganization?.zip
    }
}

private const val TAG = "HomeFragment"

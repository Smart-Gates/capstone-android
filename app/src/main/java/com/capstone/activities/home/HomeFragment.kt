package com.capstone.activities.home

import android.app.ActionBar
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.capstone.R
import com.capstone.activities.subviews.ReminderActivity
import com.capstone.activities.subviews.WeatherActivity
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var root : View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(this, Observer {
            textView.text = it
        })

        root.btn_weather.setOnClickListener {
            val intent = Intent(getActivity(), WeatherActivity::class.java)
            startActivity(intent)
        }

        root.btn_reminder.setOnClickListener {
            val intent = Intent(getActivity(), ReminderActivity::class.java)
            startActivity(intent)
        }

        populateReminderPanel()
        populateEventPanel()
        return root
    }

    private fun populateReminderPanel() {

    }

    private fun populateEventPanel() {
        val panel: RelativeLayout = root.findViewById(R.id.events_panel)
        val event = RelativeLayout(activity)
        val size: ViewGroup.LayoutParams = event.layoutParams
        size.width = MATCH_PARENT
        size.height = 50
        event.layoutParams = size
        event.setBackgroundColor(0x00)
        panel.addView(event)
    }
}
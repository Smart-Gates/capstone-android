package com.capstone.events

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.capstone.Calendar_view
import com.capstone.R
import kotlinx.android.synthetic.main.add_event.*

class AddEvent : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_event)
        var btnSubmit = findViewById<Button>(R.id.btn_event_submit)

        btnSubmit.setOnClickListener {
            val title = editEventTitle.text.toString().trim()
            val description = editEventDescription.text.toString().trim()
            val location = editEventLocation.text.toString().trim()
            val startTime = editEventStartTime.text.toString().trim()
            val endTime = editEventEndTime.text.toString().trim()
            val attendees = editEventAttendee.text.toString().trim()
        }
        editEventStartTime.setOnClickListener {
            val intent = Intent(this, Calendar_view::class.java)
            startActivity(intent) }
    }
}
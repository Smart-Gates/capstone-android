package com.capstone.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.R
import kotlinx.android.synthetic.main.edit_time.*
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class EditTime : AppCompatActivity () {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_time)
        val dateIn = intent.extras?.get("time_object") as String
        if (dateIn != "") {
            val dateFormatter =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val date = dateFormatter.parse(dateIn)

            val hourFormat = SimpleDateFormat("h")
            val minuteFormat = SimpleDateFormat("mm")
            val dayFormat = SimpleDateFormat("d")
            val monthFormat = SimpleDateFormat("MM")
            val yearFormat = SimpleDateFormat("yyyy")

            var hour = hourFormat.format(date).toInt()
            var minute = minuteFormat.format(date).toInt()
            var day = dayFormat.format(date).toInt()
            var month = monthFormat.format(date).toInt()
            var year = yearFormat.format(date).toInt()

            timePicker.hour = hour
            timePicker.minute = minute

            datePicker.updateDate(year, month, day)
        }


        btn_time_edit_submit.setOnClickListener {
            val cal = Calendar.getInstance()
            cal.set(datePicker.year, datePicker.month, datePicker.dayOfMonth,
                timePicker.hour, timePicker.minute, 0)

            val date = cal.time
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val newDate = dateFormatter.format(date)
            returnDate(newDate)
        }

    }

    private fun returnDate (newDate: String) {
        val dateResult = Intent()
        dateResult.putExtra("time_object", newDate as Serializable)
        setResult(Activity.RESULT_OK, dateResult)
        finish()
        return
    }

}
package com.capstone

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.capstone.events.AddEvent
import kotlinx.android.synthetic.main.activity_calendar_view.*
import java.util.*

class Calendar_view : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_view)
        val c= Calendar.getInstance()
        val year=c.get(Calendar.YEAR)
        val month=c.get(Calendar.MONTH)
        val day=c.get(Calendar.DAY_OF_MONTH)
        date_texti.setOnClickListener {
            val dpd= DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { view, myear, mmonth, mdayOfMonth ->
                    selectdate.setText(""+myear+"-"+mmonth+"-"+mdayOfMonth)  },year,month,day)
            dpd.show()

        }
        starttime_text.setOnClickListener {
            val start_time= TimePickerDialog(this,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute -> select_stime.setText(""+hourOfDay+":"+minute)},c.get(
                    Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),false )
           start_time.show()
        }

        endtime_text.setOnClickListener {
            val end_time= TimePickerDialog(this,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute -> select_etime.setText(""+hourOfDay+":"+minute)},c.get(
                    Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),true )
            end_time.show()
        }
        btnCancel.setOnClickListener {
            val intent = Intent(this, AddEvent::class.java)
            startActivity(intent) }

        val descript=findViewById(R.id.txtItem) as EditText
        val getdescript=descript.text
        val title=findViewById(R.id.title_txt) as EditText
        val gettitle=title.text
        val location=findViewById(R.id.location) as EditText
        val getloc=location.text
        val attendee=findViewById(R.id.attendee) as EditText
        val getatt=attendee.text


    }
}

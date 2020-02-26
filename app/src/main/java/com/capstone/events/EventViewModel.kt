package com.capstone.events

import androidx.lifecycle.ViewModel
import com.capstone.models.events.Event
import com.capstone.models.events.EventsList

class EventViewModel : ViewModel() {
    private var events : List<Event>? = null

    fun setEvents (initList: List<Event>?) {
        events = initList
    }

    fun  getEvent (index: Int): Event? {
        return events?.get(index)
    }

}
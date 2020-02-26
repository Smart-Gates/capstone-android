package com.capstone.events

import androidx.lifecycle.ViewModel
import com.capstone.models.events.Event
import com.capstone.models.events.EventsList

class EventViewModel : ViewModel() {
    private var events : EventsList? = null

    fun setEvents (initList: List<Event>?) {
        events = initList?.let { EventsList(it) }
    }

    fun  getEvent (index: Int): Event? {
        return events?.content?.get(0)
    }

}
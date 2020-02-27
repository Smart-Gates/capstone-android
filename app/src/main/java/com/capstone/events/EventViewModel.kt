package com.capstone.events

import androidx.lifecycle.ViewModel
import com.capstone.models.events.Event
import com.capstone.models.events.EventsList

class EventViewModel : ViewModel() {
    private var events : EventsList? = null

    fun setEvents (initList: EventsList?) {
        events = initList
    }

    fun  getEvent (index: Int): Event? {
        return events?.content?.get(index)
    }

    fun getEventsList (): EventsList? {
        return events
    }

}
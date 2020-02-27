package com.capstone.events

import androidx.lifecycle.ViewModel
import com.capstone.models.events.Event
import com.capstone.models.events.EventList

class EventViewModel : ViewModel() {
    private var events : EventList? = null

    fun setEvents (initList: EventList?) {
        events = initList
    }

    fun  getEvent (index: Int): Event? {
        return events?.content?.get(index)
    }

    fun getEventsList (): EventList? {
        return events
    }

}
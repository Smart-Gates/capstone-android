package com.capstone.ui.events

import androidx.lifecycle.ViewModel
import com.capstone.api.response.events.Event
import com.capstone.api.response.events.EventList

class EventViewModel : ViewModel() {
    private var events : EventList? = null

    fun setEvents (initList: EventList?) {
        events = initList
        events!!.content = events!!.content.asReversed()
    }

    fun  getEvent (index: Int): Event? {
        return events?.content?.get(index)
    }

    fun getEventsList (): EventList? {
        return events
    }

}
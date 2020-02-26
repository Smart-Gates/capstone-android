package com.capstone.models.events
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class EventResponse {
    @SerializedName("eventList")
    @Expose
    var eventList: List<Event>? = null
}
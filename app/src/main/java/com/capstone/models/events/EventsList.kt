package com.capstone.models.events

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class EventsList {
    @SerializedName("content")
    @Expose
    val content : List<Event>? = null
}
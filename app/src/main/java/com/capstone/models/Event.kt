package com.capstone.models

data class Event(
    val created_at: String,
    val updated_at: String,
    val id: Int,
    val title: String,
    val description: String,
    val location: String,
    val start_time: String,
    val end_time: String,
    val creator: EventCreator
)
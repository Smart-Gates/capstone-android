package com.capstone.models.reminders
import com.capstone.models.User
import com.capstone.models.events.Event

/** Response from POST of a new method or a single event
 * */
data class Reminder(
    val created_at: String,
    val updated_at: String,
    val id: Long,
    val title: String,
    val description: String,
    val event: Event,
    val start_time: String,
    val creator: User
)

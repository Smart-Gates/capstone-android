package com.capstone.models.events
import com.capstone.models.User

/** Response from POST of a new method or a single event
 * */
data class Event(
    val created_at: String,
    val updated_at: String,
    val id: Long,
    val title: String,
    val description: String,
    val location: String,
    val start_time: String,
    val end_time: String,
    val creator: User,
    val attendees: List<User>
)

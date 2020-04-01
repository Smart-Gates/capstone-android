package com.capstone.api.response.events
import com.capstone.api.response.User
import java.io.Serializable

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
) :Serializable

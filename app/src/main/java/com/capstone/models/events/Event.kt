package com.capstone.models.events
import com.capstone.models.User
import java.sql.Timestamp

/** Response from POST of a new method or a single event
 * */
data class Event(
    val created_at: String,
    val updated_at: String,
    val id: Int,
    val title: String,
    val description: String,
    val location: String,
    val start_time: Timestamp,
    val end_time: Timestamp,
    val creator: User
    // val reminder: Reminder // need to implement reminder data object
)

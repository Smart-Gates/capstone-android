package com.capstone.models.events

import java.sql.Timestamp

/** payload to be sent to the API ON POST
 * */
data class EventPayload (
    val title: String,
    val description: String,
    val location: String,
    val start_time: Timestamp,
    val end_time: Timestamp,
    val attendee_id: MutableList<String>
    )

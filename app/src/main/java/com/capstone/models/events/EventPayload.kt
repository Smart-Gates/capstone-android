package com.capstone.models.events

import java.sql.Timestamp

/** payload to be sent to the API ON POST
 * */
data class EventPayload (
    val title: String,
    val description: String,
    val location: String,
    val start_time: String,
    val end_time: String,
    val attendee_id: Array<String>
    ) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EventPayload

        if (!attendee_id.contentEquals(other.attendee_id)) return false

        return true
    }

    override fun hashCode(): Int {
        return attendee_id.contentHashCode()
    }
}

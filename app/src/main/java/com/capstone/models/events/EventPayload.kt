package com.capstone.models.events

/** payload to be sent to the API ON POST
 * */
data class EventPayload (
    val title: String,
    val description: String,
    val location: String,
    val start_time: String,
    val end_time: String,
    val attendee_email: Array<String>
    ) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EventPayload

        if (!attendee_email.contentEquals(other.attendee_email)) return false

        return true
    }

    override fun hashCode(): Int {
        return attendee_email.contentHashCode()
    }
}

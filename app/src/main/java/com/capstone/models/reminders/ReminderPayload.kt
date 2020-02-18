package com.capstone.models.reminders

import java.sql.Timestamp

/** payload to be sent to the API ON POST
 * */
data class ReminderPayload(
    val title: String,
    val description: String,
    val start_time: Timestamp
)

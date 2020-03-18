package com.capstone.models.reminders

data class ReminderPayload(
    val title: String,
    val description: String,
    val start_time: String
)

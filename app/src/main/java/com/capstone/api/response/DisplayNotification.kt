package com.capstone.api.response

data class DisplayNotification(
    var notificationId: Int,
    var messageTitle: String,
    var messageBody: String,
    var timeInMs: Long
)
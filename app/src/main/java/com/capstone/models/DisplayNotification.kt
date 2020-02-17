package com.capstone.models

data class DisplayNotification(
    var notificationId: Int,
    var messageTitle: String,
    var messageBody: String,
    var timeInMs: Long
)
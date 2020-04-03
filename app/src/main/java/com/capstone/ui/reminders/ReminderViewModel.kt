package com.capstone.ui.reminders

import androidx.lifecycle.ViewModel
import com.capstone.api.response.reminders.Reminder
import com.capstone.api.response.reminders.ReminderList

class ReminderViewModel : ViewModel() {
    private var reminders : ReminderList? = null

    fun setReminders (initList: ReminderList?) {
        reminders = initList
        reminders!!.content = reminders!!.content.asReversed()
    }

    fun  getReminder (index: Int): Reminder? {
        return reminders?.content?.get(index)
    }

    fun getRemindersList (): ReminderList? {
        return reminders
    }

}
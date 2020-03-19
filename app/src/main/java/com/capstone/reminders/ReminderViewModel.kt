package com.capstone.reminders

import androidx.lifecycle.ViewModel
import com.capstone.models.reminders.Reminder
import com.capstone.models.reminders.ReminderList

class ReminderViewModel : ViewModel() {
    private var reminders : ReminderList? = null

    fun setReminders (initList: ReminderList?) {
        reminders = initList
    }

    fun  getReminder (index: Int): Reminder? {
        return reminders?.content?.get(index)
    }

    fun getRemindersList (): ReminderList? {
        return reminders
    }

}
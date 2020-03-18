package com.capstone.reminders

import androidx.lifecycle.ViewModel
import com.capstone.models.reminders.Reminder
import com.capstone.models.reminders.RemindersList

class ReminderViewModel : ViewModel() {
    private var reminders : RemindersList? = null

    fun setReminders (initList: RemindersList?) {
        reminders = initList
    }

    fun  getReminder (index: Int): Reminder? {
        return reminders?.content?.get(index)
    }

    fun getRemindersList (): RemindersList? {
        return reminders
    }

}
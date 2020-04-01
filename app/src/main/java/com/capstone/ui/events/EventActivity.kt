package com.capstone.ui.events

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.R

class EventActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventRequest()
    }

    private fun eventRequest() {
        val sharedPrefs = getSharedPreferences(
            getString(R.string.shared_preferences_key),
            Context.MODE_PRIVATE
        )
        val editor: SharedPreferences.Editor = sharedPrefs.edit()
        val accessToken = sharedPrefs.getString(getString(R.string.access_token), null);
    }
}


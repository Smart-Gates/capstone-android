package com.capstone.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.api.Retrofit2Client
import com.capstone.models.LoginPayload
import kotlinx.android.synthetic.main.login_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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


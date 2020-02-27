package com.capstone

import android.app.Service
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.CapService
import com.capstone.R
import com.capstone.api.Retrofit2Client
import com.capstone.models.LoginPayload
import com.capstone.models.LoginResponse
import kotlinx.android.synthetic.main.login_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        // get reference to all views
        var btnSubmit = findViewById<Button>(R.id.btn_submit)

        // set on-click listener
        btnSubmit.setOnClickListener {


            val email = editEmail.text.toString().trim()
            val password = editPassword.text.toString().trim()

            if (email.isEmpty()) {
                editEmail.error = "email address is required"
                editEmail.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                editPassword.error = "password is required required"
                editPassword.requestFocus()
                return@setOnClickListener
            }
            loginRequest(email, password)
        }
    }

    private fun loginRequest(email: String, password: String) {
        val payload = LoginPayload(email, password)

        Retrofit2Client.instance.loginUser(payload)
            .enqueue(object : Callback<LoginResponse> {
                override fun onFailure(call: Call<LoginResponse>?, t: Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(
                    call: Call<LoginResponse>?,
                    response: Response<LoginResponse>
                ) {
                    // check to make sure that it was a successful return to server
                    if (response.code() == 200) {
                        // write the users access token to Shared Preferences
                        val sharedPrefs = getSharedPreferences(
                            getString(R.string.shared_preferences_key),
                            Context.MODE_PRIVATE
                        )
                        val editor: SharedPreferences.Editor = sharedPrefs.edit()
                        // store access token
                        editor.putString(
                            getString(R.string.access_token),
                            response.body()?.accessToken
                        )
                        //  put logged in flag
                        editor.putBoolean(
                            getString(R.string.is_logged_in_key),
                            true
                        )
                        editor.apply()
                        // update the Firebase cloud messaging token
                        updateFCM()
                        // finish returns to main activity
                        finish()
                        return
                    }
                    // if it is not a proper response then show the toast
                    Toast.makeText(
                        applicationContext,
                        response.body()?.toString(),
                        Toast.LENGTH_SHORT
                    ).show()


                }
            })

    }

    fun updateFCM() {
        val sharedPref = this.getSharedPreferences(
            getString(R.string.shared_preferences_key), Service.MODE_PRIVATE
        )
        // exclamation marks is to ignore nullability
        var auth = sharedPref.getString(getString(R.string.access_token), "")!!
        auth = "Bearer $auth"
        val token = sharedPref.getString(getString(R.string.fcm_token), "")!!

        CapService().setFCMTokenToServer(token, auth)
    }
}
package com.capstone.loginActivity


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        // get reference to all views
        var et_user_name = findViewById(R.id.et_user_name) as EditText
        var et_password = findViewById(R.id.et_password) as EditText
        var btn_reset = findViewById(R.id.btn_reset) as Button
        var btn_submit = findViewById(R.id.btn_submit) as Button

        btn_reset.setOnClickListener {
            // clearing user_name and password edit text views on reset button click
            et_user_name.setText("")
            et_password.setText("")
        }

        // set on-click listener
        btn_submit.setOnClickListener {
            val username = et_user_name.text;
            val password = et_password.text;
            Toast.makeText(this@LoginActivity, username, Toast.LENGTH_LONG).show()

            if (username.toString() == "dbarak" && password.toString() == "1234") {
                setContentView(R.layout.weather_activity)
            } else {
                Toast.makeText(this@LoginActivity, "Incorrect Login Information", Toast.LENGTH_SHORT).show();
            }


        }
    }
}
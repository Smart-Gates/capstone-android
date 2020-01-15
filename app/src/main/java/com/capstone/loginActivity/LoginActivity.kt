package com.capstone.loginActivity


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.capstone.weatherapp.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        // get reference to all views
        var etUserName = findViewById<EditText>(R.id.et_user_name)
        var etPassword = findViewById<EditText>(R.id.et_password)
        var btnReset = findViewById<Button>(R.id.btn_reset)
        var btnSubmit = findViewById<Button>(R.id.btn_submit)

        btnReset.setOnClickListener {
            // clearing user_name and password edit text views on reset button click
            etUserName.setText("")
            etPassword.setText("")
        }

        // set on-click listener
        btnSubmit.setOnClickListener {
            val username = etUserName.text
            val password = etPassword.text
            Toast.makeText(this@LoginActivity, username, Toast.LENGTH_LONG).show()

            if (username.toString() == "dbarak" && password.toString() == "1234") {
                setContentView(R.layout.weather_activity)
            } else {
                Toast.makeText(this@LoginActivity, "Incorrect Login Information", Toast.LENGTH_SHORT).show()
            }


        }
    }
}
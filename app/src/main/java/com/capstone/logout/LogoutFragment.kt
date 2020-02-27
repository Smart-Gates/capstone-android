package com.capstone.logout

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.capstone.R
import com.capstone.MainActivity
import com.capstone.activities.logout.LogoutViewModel

class LogoutFragment : Fragment() {

    private lateinit var logoutViewModel: LogoutViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        logoutViewModel =
            ViewModelProviders.of(this).get(LogoutViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_logout, container, false)
        var btnLogout = root.findViewById<Button>(R.id.logout_button)
        val sharedPrefs =
            this.activity!!.getSharedPreferences(getString(R.string.shared_preferences_key),
                Context.MODE_PRIVATE)


        btnLogout.setOnClickListener {

            val editor: SharedPreferences.Editor = sharedPrefs.edit()
            // store access token
            editor.putString(
                getString(R.string.access_token),
                ""
            )
            //  put logged out flag
            editor.putBoolean(
                getString(R.string.is_logged_in_key),
                false
            )
            editor.apply()
            // finish returns to main activity
            val intent = Intent(getActivity(), MainActivity::class.java)
            startActivity(intent)
        }



        return root
    }
}
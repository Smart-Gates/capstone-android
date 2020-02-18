package com.capstone

import android.Manifest
import android.app.Activity
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.capstone.api.Retrofit2Client
import com.capstone.models.FCMTokenPayload
import com.capstone.models.FCMTokenResponse
import com.google.firebase.iid.FirebaseInstanceId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


internal class CapService : Service() {


    fun checkPermissions(activity: Activity): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (permissionState == PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1
            )
        }
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    fun deleteFirebaseToken() {
        val thread = Thread(Runnable {
            try {
                FirebaseInstanceId.getInstance().deleteInstanceId()
            } catch (e: Exception) {
                Log.e("ERROR", Log.getStackTraceString(e))
            }
        })
        thread.start()
    }

    fun setFCMTokenToServer(token: String, auth: String) {
        val payload = FCMTokenPayload(token)
        Log.d("FCM_TOKEN", "token to be sent $token")

        Retrofit2Client.instance.updateFCMToken(auth, payload)
            .enqueue(object : Callback<FCMTokenResponse> {
                override fun onFailure(call: Call<FCMTokenResponse>?, t: Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(
                    call: Call<FCMTokenResponse>?,
                    response: Response<FCMTokenResponse>
                ) {
                    Log.d("FCM_TOKEN", response.body().toString())
                }
            })
    }
}


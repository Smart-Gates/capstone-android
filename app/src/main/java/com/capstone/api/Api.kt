package com.capstone.api

import Weather
import com.capstone.models.EventList
import com.capstone.models.LoginPayload
import com.capstone.models.LoginResponse
import retrofit2.Call
import retrofit2.http.*


public interface Api {

    @Headers("Content-Type: application/json")
    @POST("api/auth/signin")
    fun loginUser(
        @Body loginPayload: LoginPayload
    ): Call<LoginResponse>

    @Headers("Content-Type: application/json")
    @GET("api/weather/{longitude}/{latitude}")
    fun getWeather(
        @Header("Authorization") auth: String, @Path("longitude") longitude: String?, @Path("latitude") latitude: String?
    ): Call<Weather>

    @Headers("Headers: Authorization: Bearer {accessToken}")
    @GET("/api/events")
    fun getEvents(
        @Header("Authorization") auth: String
    ): Call<EventList>
}
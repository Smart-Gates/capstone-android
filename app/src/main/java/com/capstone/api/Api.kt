package com.capstone.api

import Weather
import com.capstone.models.*
import com.capstone.models.events.EventList
import com.capstone.models.events.Event
import com.capstone.models.events.EventPayload
import com.capstone.models.reminders.Reminder
import com.capstone.models.reminders.ReminderList
import com.capstone.models.reminders.ReminderPayload
import okhttp3.ResponseBody
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
        @Header("Authorization") auth: String,
        @Path("longitude") longitude: String?,
        @Path("latitude") latitude: String?
    ): Call<Weather>

    // the second header for x-spring-data removes the _embedded tag from response
    @Headers("Content-Type: application/json", "Accept: application/x-spring-data-verbose+json")
    @GET("/api/events")
    fun getEvents(
        @Header("Authorization") auth: String
    ): Call<EventList>

    @Headers("Content-Type: application/json")
    @POST("api/events")
    fun createEvent(
        @Header("Authorization") auth: String,
        @Body eventPayload: EventPayload
    ): Call<Event>

    @Headers("Content-Type: application/json")
    @PUT("api/events/{event_id}")
    fun editEvent(
        @Header("Authorization") auth: String,
        @Path("event_id") event_id: String,
        @Body eventPayload: EventPayload
    ): Call<Event>

    @Headers("Content-Type: application/json")
    @DELETE("api/events/{event_id}")
    fun deleteEvent(
        @Header("Authorization") auth: String,
        @Path("event_id") event_id: String
    ): Call<ResponseBody>

    // the second header for x-spring-data removes the _embedded tag from response
    @Headers("Content-Type: application/json", "Accept: application/x-spring-data-verbose+json")
    @GET("/api/reminders")
    fun getReminders(
        @Header("Authorization") auth: String
    ): Call<ReminderList>

    @Headers("Content-Type: application/json")
    @POST("api/notification/token")
    fun updateFCMToken(
        @Header("Authorization") auth: String,
        @Body fcmTokenPayload: FCMTokenPayload
    ): Call<FCMTokenResponse>

    @Headers("Content-Type: application/json")
    @POST("api/reminders")
    fun createReminder(
        @Header("Authorization") auth: String,
        @Body reminderPayload: ReminderPayload
    ): Call<Reminder>

    @Headers("Content-Type: application/json")
    @PUT("api/events/{reminder_id}")
    fun editReminder(
        @Header("Authorization") auth: String,
        @Path("reminder_id") event_id: String,
        @Body reminderPayload: ReminderPayload
    ): Call<Reminder>

    @Headers("Content-Type: application/json")
    @DELETE("api/events/{reminder_id}")
    fun deleteReminder(
        @Header("Authorization") auth: String,
        @Path("reminder_id") event_id: String
    ): Call<ResponseBody>

    @GET("api/organizations")
    fun getUsersOrg(
        @Header("Authorization") auth: String
    ): Call<OrganizationResponse>
}
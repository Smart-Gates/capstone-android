package com.capstone.models.events
import com.capstone.models.User
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

/** Response from POST of a new method or a single event
 * */
class Event {
    @SerializedName("created_at")
    @Expose
    val created_at: String? = null

    @SerializedName("updated_at")
    @Expose
    val updated_at: String? = null

    @SerializedName("id")
    @Expose
    val id: Int? = null

    @SerializedName("title")
    @Expose
    val title: String? = null

    @SerializedName("description")
    @Expose
    val description: String? = null

    @SerializedName("location")
    @Expose
    val location: String? = null

    @SerializedName("start_time")
    @Expose
    val start_time: Timestamp? = null

    @SerializedName("end_time")
    @Expose
    val end_time: Timestamp? = null

    @SerializedName("creator")
    @Expose
    val creator: User? = null
}

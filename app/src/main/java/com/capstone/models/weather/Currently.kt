import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

// Serialized name is used to map the var to the json value
class Currently {
    @SerializedName("time")
    @Expose
    var time: Long? = null
    @SerializedName("summary")
    @Expose
    var summary: String? = null
    @SerializedName("icon")
    @Expose
    var icon: String? = null
    @SerializedName("precipIntensity")
    @Expose
    var precipIntensity: Double? = null
    @SerializedName("precipProbability")
    @Expose
    var precipProbability: Double? = null
    @SerializedName("precipType")
    @Expose
    var precipType: Any? = null
    @SerializedName("temperature")
    @Expose
    var temperature: Double? = null
    @SerializedName("humidity")
    @Expose
    var humidity: Double? = null
    @SerializedName("cloudCover")
    @Expose
    var cloudCover: Double? = null
    @SerializedName("visibility")
    @Expose
    var visibility: Double? = null

}
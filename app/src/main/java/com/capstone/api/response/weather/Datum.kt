import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Datum {
    @SerializedName("time")
    @Expose
    var time: Int? = null
    @SerializedName("precipIntensityMax")
    @Expose
    var precipIntensityMax: Double? = null
    @SerializedName("precipIntensityMaxTime")
    @Expose
    var precipIntensityMaxTime: Double? = null
    @SerializedName("precipProbability")
    @Expose
    var precipProbability: Double? = null
    @SerializedName("precipType")
    @Expose
    var precipType: String? = null
    @SerializedName("temperatureMin")
    @Expose
    var temperatureMin: Double? = null
    @SerializedName("temperatureMinTime")
    @Expose
    var temperatureMinTime: Double? = null
    @SerializedName("temperatureMax")
    @Expose
    var temperatureMax: Double? = null
    @SerializedName("temperatureMaxTime")
    @Expose
    var temperatureMaxTime: Double? = null
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
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Weather {
    @SerializedName("latitude")
    @Expose
    var latitude: String? = null
    @SerializedName("longitude")
    @Expose
    var longitude: String? = null
    @SerializedName("timezone")
    @Expose
    var timezone: String? = null
    @SerializedName("currently")
    @Expose
    var currently: Currently? = null
    @SerializedName("daily")
    @Expose
    var daily: Daily? = null

}
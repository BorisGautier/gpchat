package cm.tbg.gpchat.placespicker.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Meta(
        @SerializedName("code")
        val code: Int,
        @SerializedName("requestId")
        val requestId: String
)
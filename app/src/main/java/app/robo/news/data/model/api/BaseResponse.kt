package app.robo.news.data.model.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

abstract class BaseResponse : Serializable {

    @SerializedName("code")
    var code: Int = -1

    @SerializedName("success")
    var success: Boolean = false

    @SerializedName("message")
    var message = ""

    override fun toString(): String {
        return "BaseResponse(code=$code, success=$success, message='$message')"
    }

}

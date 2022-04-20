package app.robo.news.data.model.api

import app.robo.news.data.model.other.News
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class NewsListResponse : BaseResponse(), Serializable {

    @SerializedName("totalResults")
    var totalResults: Int = 0

    @SerializedName("articles")
    var newsList: ArrayList<News> = ArrayList()

}


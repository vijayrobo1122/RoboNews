package app.robo.news.data.model.other

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class News : Serializable {

    @SerializedName("source")
    var source: Source = Source()

    @SerializedName("author")
    var author: String? = ""

    @SerializedName("title")
    var title: String? = ""

    @SerializedName("description")
    var description: String? = ""

    @SerializedName("url")
    var url: String? = ""

    @SerializedName("urlToImage")
    var urlToImage: String? = ""

    @SerializedName("publishedAt")
    var publishedAt: String? = ""

    @SerializedName("content")
    var content: String? = ""

    class Source : Serializable {

        @SerializedName("id")
        var id: String? = ""

        @SerializedName("name")
        var name: String? = ""
    }
}


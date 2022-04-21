package app.robo.news.data.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.robo.news.data.model.api.NewsListResponse
import app.robo.news.data.remote.ApiResult
import app.robo.news.data.remote.ApiSource
import app.robo.news.data.remote.SafeApiRequest
import app.robo.news.utils.getJsonDataFromAsset
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class NewsRepository constructor(private val context: Context, private val apiSource: ApiSource) :
    SafeApiRequest() {

    companion object {
        private val TAG: String = NewsRepository::class.java.simpleName
    }

    /**
     * making static page size 1 because of showing 1 top news in screen
     */
    suspend fun getTopHeadLineNewsList(
        country: String = "in", category: String = "sports",
        pageSize: Int = 1, page: Int = 1
    ): LiveData<ApiResult<NewsListResponse>> {
        Log.d(TAG, "getTopHeadLineNewsList")
        /*return performNetworkOperation(networkCall = {
            apiRequest {
                apiSource.getTopHeadlineNewsList(
                    country = country, category = category,
                    pageSize = pageSize, page = page
                )
            }
        })*/
        val jsonFileString = context.getJsonDataFromAsset("top_headline_records.json")
        val listNewsType = object : TypeToken<NewsListResponse>() {}.type
        val response: NewsListResponse = Gson().fromJson(jsonFileString, listNewsType)
        val liveData: MutableLiveData<ApiResult<NewsListResponse>> = MutableLiveData()
        liveData.value = ApiResult.success(response, null, 200)
        return liveData
    }

    /**
     * getting result of IPL news
     */
    suspend fun getPopularNewsList(
        country: String = "in", category: String = "sports",
        pageSize: Int = 10, page: Int = 1, search: String = "IPL"
    ): LiveData<ApiResult<NewsListResponse>> {
        Log.d(TAG, "getPopularNewsList")
        /*return performNetworkOperation(networkCall = {
            apiRequest {
                apiSource.getPopularNewsList(
                    country = country, category = category,
                    pageSize = pageSize, page = page, search = search
                )
            }
        })*/
        val jsonFileString = context.getJsonDataFromAsset("ipl_records.json")
        val listNewsType = object : TypeToken<NewsListResponse>() {}.type
        val response: NewsListResponse = Gson().fromJson(jsonFileString, listNewsType)
        val liveData: MutableLiveData<ApiResult<NewsListResponse>> = MutableLiveData()
        liveData.value = ApiResult.success(response, null, 200)
        return liveData
    }
}
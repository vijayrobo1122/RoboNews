package app.robo.news.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import app.robo.news.data.model.api.NewsListResponse
import app.robo.news.data.remote.ApiResult
import app.robo.news.data.remote.ApiSource
import app.robo.news.data.remote.SafeApiRequest
import app.robo.news.data.remote.performNetworkOperation

class NewsRepository constructor(private val apiSource: ApiSource) : SafeApiRequest() {

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
        return performNetworkOperation(networkCall = {
            apiRequest {
                apiSource.getTopHeadlineNewsList(
                    country = country, category = category,
                    pageSize = pageSize, page = page
                )
            }
        })
    }

    /**
     * getting result of IPL news
     */
    suspend fun getPopularNewsList(
        country: String = "in", category: String = "sports",
        pageSize: Int = 10, page: Int = 1, search: String = "IPL"
    ): LiveData<ApiResult<NewsListResponse>> {
        Log.d(TAG, "getPopularNewsList")
        return performNetworkOperation(networkCall = {
            apiRequest {
                apiSource.getPopularNewsList(
                    country = country, category = category,
                    pageSize = pageSize, page = page, search = search
                )
            }
        })
    }
}
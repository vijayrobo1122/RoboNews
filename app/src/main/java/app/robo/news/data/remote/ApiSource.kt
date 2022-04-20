package app.robo.news.data.remote

import app.robo.news.data.model.api.NewsListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiSource {

    @GET("top-headlines")
    suspend fun getTopHeadlineNewsList(
        @Query("country") country: String,
        @Query("category") category: String,
        @Query("pageSize") pageSize: Int,
        @Query("page") page: Int
    ): NewsListResponse

    @GET("top-headlines")
    suspend fun getPopularNewsList(
        @Query("country") country: String,
        @Query("category") category: String,
        @Query("pageSize") pageSize: Int,
        @Query("page") page: Int,
        @Query("q") search: String
    ): NewsListResponse
}
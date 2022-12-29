package com.nafian.newsapp.data.remote

import com.nafian.newsapp.data.model.NewsResponse
import com.nafian.newsapp.util.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    /**
     * retrofit service untuk mengambil top-headlines.
     */
    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country") countryCode: String = "id",
        @Query("page") pageNumber: Int = 1,
        @Query("apiKey") apiKey: String = API_KEY
    ): Response<NewsResponse>

    /**
     * retrofit service untuk mengambil semua berita yang ada dan juga untuk melakukan pencarian
     * berita.
     */
    @GET("v2/everything")
    suspend fun searchForNews(
        @Query("q") searchQuery: String,
        @Query("page") pageNumber: Int = 1,
        @Query("apiKey") apiKey: String = API_KEY
    ): Response<NewsResponse>

}
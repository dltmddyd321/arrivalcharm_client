package com.example.arrivalcharm.api

import com.example.arrivalcharm.datamodel.Post
import retrofit2.http.GET
import retrofit2.http.Query

interface TestApiService {
    @GET("posts")
    suspend fun getPosts(
        @Query("_page") page: Int,
        @Query("_limit") pageSize: Int
    ): List<Post>
}
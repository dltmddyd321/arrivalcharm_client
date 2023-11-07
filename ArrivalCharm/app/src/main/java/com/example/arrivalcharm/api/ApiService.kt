package com.example.arrivalcharm.api

import com.example.arrivalcharm.datamodel.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("/advice/search/{query}")
    suspend fun fetchAdvice(
        @Path("query") query: String = "destination"
    ): Response<AdviceResultModel>

    @POST("/api/v1/auth/login/oauth")
    suspend fun requestLogin(
        @Body oAuth2UserInfo: UserLoginInfo
    ): Response<LoginResultModel>

    @POST("/api/v1/auth/accessToken")
    suspend fun tokenRefresh(
        @Body refreshToken: RefreshTokenBody
    ): Response<Unit>

    @GET("/api/v1/destination")
    suspend fun getDestinationList(
        @HeaderMap headers: HashMap<String, String>
    ): Response<Unit>

    @POST("/api/v1/destination")
    suspend fun updateDestination(
        @HeaderMap headers: HashMap<String, String>,
        @Query("address") address: String,
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("name") name: String
    ): Response<Destination>

    @GET("/api/v1/destination/recent")
    suspend fun getRecentList(
        @HeaderMap headers: HashMap<String, String>
    ): Response<Unit>

    @Multipart
    @PUT("/api/v1/users/{id}")
    suspend fun editUserInfo(
        @HeaderMap headers: HashMap<String, String>,
        @Query("displayUsername") displayUsername: String,
        @Part file : MultipartBody.Part,
        @Path("id") id: Int
    ): Response<Unit>

    @PUT("/api/v1/destination/{id}")
    suspend fun editDestination(
        @HeaderMap headers: HashMap<String, String>,
        @Path("id") id: Int,
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("name") name: String
    ): Response<Destination>

    @DELETE("/api/v1/destination/{id}")
    suspend fun deleteDestination(
        @HeaderMap headers: HashMap<String, String>,
        @Path("id") id: Int
    ): Response<Unit>

    @GET("/api/v1/destination/{id}")
    suspend fun fetchDestination(
        @HeaderMap headers: HashMap<String, String>,
        @Path("id") id: Int
    ): Response<Destination>
}
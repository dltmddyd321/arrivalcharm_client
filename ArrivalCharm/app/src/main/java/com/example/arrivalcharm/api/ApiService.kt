package com.example.arrivalcharm.api

import com.example.arrivalcharm.datamodel.AdviceResultModel
import com.example.arrivalcharm.datamodel.LoginResultModel
import com.example.arrivalcharm.datamodel.RefreshTokenBody
import com.example.arrivalcharm.datamodel.UserLoginInfo
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
    ): Response<Unit>
}
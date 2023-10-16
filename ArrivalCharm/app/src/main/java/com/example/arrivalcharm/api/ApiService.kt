package com.example.arrivalcharm.api

import com.example.arrivalcharm.datamodel.AdviceResultModel
import com.example.arrivalcharm.datamodel.LoginResultModel
import com.example.arrivalcharm.datamodel.UserLoginInfo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("/advice/search/{query}")
    suspend fun fetchAdvice(
        @Path("query") query: String = "destination"
    ): Response<AdviceResultModel>

    @POST("/api/v1/auth/login/oauth")
    suspend fun requestLogin(
        @Body oAuth2UserInfo: UserLoginInfo
    ): Response<LoginResultModel>
}
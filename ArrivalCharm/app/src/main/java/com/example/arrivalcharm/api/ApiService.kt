package com.example.arrivalcharm.api

import com.example.arrivalcharm.datamodel.AdviceResultModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("/advice/search/{query}")
    suspend fun fetchAdvice(
        @Path("query") query: String = "destination"
    ): Response<AdviceResultModel>
}
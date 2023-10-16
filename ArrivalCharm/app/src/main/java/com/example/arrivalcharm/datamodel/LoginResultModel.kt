package com.example.arrivalcharm.datamodel

data class LoginResultModel(
    val accessToken: String,
    val name: String,
    val photo: Any,
    val refreshToken: String,
    val status: String,
    val userId: String
)
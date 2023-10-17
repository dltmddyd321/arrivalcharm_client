package com.example.arrivalcharm.datamodel

data class UserLoginInfo(
    val provider: String,
    val id: String,
    val email: String,
    val name: String,
    val password: String = "password",
)

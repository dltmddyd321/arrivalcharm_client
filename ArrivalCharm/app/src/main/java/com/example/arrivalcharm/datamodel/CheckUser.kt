package com.example.arrivalcharm.datamodel

data class CheckUser(
    val createdAt: String,
    val displayUsername: String,
    val email: String,
    val id: Int,
    val password: String,
    val profilePath: String,
    val provider: String,
    val providerId: String,
    val role: String,
    val username: String
)
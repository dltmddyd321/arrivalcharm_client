package com.example.arrivalcharm.datamodel

data class Destination(
    val address: String,
    val createdAt: String,
    val id: Int,
    val lat: String,
    val lon: String,
    val name: String,
    val user: Any,
    val userId: Int
)
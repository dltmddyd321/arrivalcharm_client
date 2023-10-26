package com.example.arrivalcharm.datamodel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location")
data class Location(
    @PrimaryKey val id: Int,
    val address: String,
    val lon: String,
    val lat: String,
    val createdAt: Long,
    val name: String = "NONE"
)
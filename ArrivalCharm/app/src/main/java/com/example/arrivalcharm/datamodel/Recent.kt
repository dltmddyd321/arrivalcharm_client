package com.example.arrivalcharm.datamodel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent")
data class Recent(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dtCreate: Long,
    val title: String
)
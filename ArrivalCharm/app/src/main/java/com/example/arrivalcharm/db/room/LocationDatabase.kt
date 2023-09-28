package com.example.arrivalcharm.db.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.arrivalcharm.datamodel.Location

@Database(entities = [Location::class], version = 1)
abstract class LocationDatabase: RoomDatabase() {
    abstract fun locationDao(): LocationDao
}
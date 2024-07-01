package com.example.arrivalcharm.db.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.arrivalcharm.datamodel.Recent

@Database(entities = [Recent::class], version = 1)
abstract class RecentDatabase : RoomDatabase() {
    abstract fun recentDao(): RecentDao
}
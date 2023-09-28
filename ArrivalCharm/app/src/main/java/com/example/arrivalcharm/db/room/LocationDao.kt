package com.example.arrivalcharm.db.room

import androidx.room.*
import com.example.arrivalcharm.datamodel.Location

@Dao
interface LocationDao {

    @Query("SELECT * FROM location")
    fun getAllLocation(): List<Location>

    @Query("SELECT * FROM location WHERE createdAt > :oneWeekAgo")
    fun getRecentLocation(oneWeekAgo: Long): List<Location>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocation(location: Location)

    @Delete
    fun deleteLocation(location: Location)
}
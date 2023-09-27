package com.example.arrivalcharm.db

import androidx.room.*
import com.example.arrivalcharm.datamodel.Location

@Dao
interface LocationDao {

    @Query("SELECT * FROM location")
    fun getAllLocation(): List<Location>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocation(location: Location)

    @Delete
    fun deleteLocation(location: Location)
}
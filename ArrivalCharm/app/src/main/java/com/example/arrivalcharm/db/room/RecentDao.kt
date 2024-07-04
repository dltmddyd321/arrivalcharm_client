package com.example.arrivalcharm.db.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.arrivalcharm.datamodel.Recent

@Dao
interface RecentDao {

    @Query("SELECT * FROM recent")
    fun getAllRecent(): List<Recent>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recent: Recent)

    @Delete
    suspend fun delete(recent: Recent)
}
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

    @Query("SELECT * FROM recent WHERE title = :title LIMIT 1")
    suspend fun getRecentByTitle(title: String): Recent?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recent: Recent)

    @Delete
    suspend fun delete(recent: Recent)

    @Transaction
    suspend fun upsert(recent: Recent) {
        val existingRecent = getRecentByTitle(recent.title)
        if (existingRecent != null) {
            delete(existingRecent)
        }
        insert(recent)
    }
}
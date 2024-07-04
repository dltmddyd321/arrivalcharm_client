package com.example.arrivalcharm.db.room

import com.example.arrivalcharm.datamodel.Recent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RecentRepository @Inject constructor(
    private val recentDao: RecentDao
) {
    suspend fun getAllRecent(): List<Recent> {
        return withContext(Dispatchers.IO) {
            recentDao.getAllRecent()
        }
    }

    fun updateRecent(recent: Recent) {
        CoroutineScope(Dispatchers.IO).launch {
            recentDao.insert(recent)
        }
    }

    fun deleteRecent(recent: Recent) {
        CoroutineScope(Dispatchers.IO).launch {
            recentDao.delete(recent)
        }
    }
}
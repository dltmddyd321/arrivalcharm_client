package com.example.arrivalcharm.db.room

import com.example.arrivalcharm.datamodel.Location
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val locationDao: LocationDao
) {
    suspend fun getAllLocation(): List<Location> {
        return withContext(Dispatchers.IO) {
            locationDao.getAllLocation()
        }
    }

    fun insertLocation(location: Location) {
        CoroutineScope(Dispatchers.IO).launch {
            locationDao.insertLocation(location)
        }
    }

    fun deleteLocation(location: Location) {
        CoroutineScope(Dispatchers.IO).launch {
            locationDao.deleteLocation(location)
        }
    }
}
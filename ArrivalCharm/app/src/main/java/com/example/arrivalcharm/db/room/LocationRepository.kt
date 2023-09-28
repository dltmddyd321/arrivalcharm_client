package com.example.arrivalcharm.db.room

import com.example.arrivalcharm.datamodel.Location
import kotlinx.coroutines.Dispatchers
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
        locationDao.insertLocation(location)
    }

    fun deleteLocation(location: Location) {
        locationDao.deleteLocation(location)
    }
}
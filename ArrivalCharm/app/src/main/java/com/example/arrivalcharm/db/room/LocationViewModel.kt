package com.example.arrivalcharm.db.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arrivalcharm.datamodel.Location
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {
    suspend fun getAllLocation(): List<Location> {
        return withContext(viewModelScope.coroutineContext) { locationRepository.getAllLocation() }
    }

    fun insertLocation(location: Location) {
        locationRepository.insertLocation(location)
    }

    fun deleteLocation(location: Location) {
        locationRepository.deleteLocation(location)
    }
}
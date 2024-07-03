package com.example.arrivalcharm.db.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arrivalcharm.datamodel.Recent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RecentViewModel @Inject constructor(
    private val recentRepository: RecentRepository
) : ViewModel() {
    suspend fun getAllRecent(): List<Recent> {
        return withContext(viewModelScope.coroutineContext) {
            recentRepository.getAllRecent()
        }
    }

    fun updateRecent(recent: Recent) {
        recentRepository.updateRecent(recent)
    }

    fun deleteRecent(recent: Recent) {
        recentRepository.deleteRecent(recent)
    }
}
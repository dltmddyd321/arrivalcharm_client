package com.example.arrivalcharm.viewmodel

import androidx.lifecycle.ViewModel
import com.example.arrivalcharm.api.ApiService
import com.example.arrivalcharm.api.NetworkModule
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserUpdateViewModel @Inject constructor(
    @NetworkModule.Main private val apiService: ApiService
) : ViewModel() {
    fun updateUserName(name: String) {
        try {

        } catch (e: Exception) {
            
        }
    }
}
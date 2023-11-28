package com.example.arrivalcharm.viewmodel

import androidx.lifecycle.ViewModel
import com.example.arrivalcharm.api.ApiService
import com.example.arrivalcharm.api.NetworkModule
import com.example.arrivalcharm.datamodel.TokenRefreshResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class TokenRefreshViewModel @Inject constructor(
    @NetworkModule.Main private val apiService: ApiService
) : ViewModel() {
    fun refreshToken(header: String): TokenRefreshResult? = runBlocking {
        try {
            val response = apiService.tokenRefresh(header)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
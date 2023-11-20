package com.example.arrivalcharm.viewmodel

import androidx.lifecycle.ViewModel
import com.example.arrivalcharm.api.ApiResult
import com.example.arrivalcharm.api.ApiService
import com.example.arrivalcharm.api.NetworkModule
import com.example.arrivalcharm.datamodel.RefreshTokenBody
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class TokenRefreshViewModel @Inject constructor(
    @NetworkModule.Main private val apiService: ApiService
) : ViewModel() {
    fun refreshToken(header: String): Flow<ApiResult<Boolean>> = flow {
        try {
            val request = RefreshTokenBody(header)
            val response = apiService.tokenRefresh(request)
            if (response.isSuccessful) {
                emit(ApiResult.Success(true))
            } else {
                emit(ApiResult.Success(false))
            }
        } catch (e: Exception) {
            emit(ApiResult.Error(e.localizedMessage ?: "An error occurred", 0))
        }
    }
}
package com.example.arrivalcharm.viewmodel

import androidx.lifecycle.ViewModel
import com.example.arrivalcharm.api.ApiResult
import com.example.arrivalcharm.api.ApiService
import com.example.arrivalcharm.api.NetworkModule
import com.example.arrivalcharm.datamodel.RefreshTokenBody
import com.example.arrivalcharm.datamodel.TokenRefreshResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class TokenRefreshViewModel @Inject constructor(
    @NetworkModule.Main private val apiService: ApiService
) : ViewModel() {
    fun refreshToken(header: String): Flow<ApiResult<TokenRefreshResult?>> = flow {
        try {
            val response = apiService.tokenRefresh(header)
            if (response.isSuccessful) {
                emit(ApiResult.Success(response.body()))
            } else {
                emit(ApiResult.Error("Failed", response.code()))
            }
        } catch (e: Exception) {
            emit(ApiResult.Error(e.localizedMessage ?: "An error occurred", 0))
        }
    }
}
package com.example.arrivalcharm.viewmodel

import androidx.lifecycle.ViewModel
import com.example.arrivalcharm.api.ApiResult
import com.example.arrivalcharm.api.ApiService
import com.example.arrivalcharm.api.NetworkModule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class AdviceViewModel @Inject constructor(
    @NetworkModule.Advice private val apiService: ApiService
): ViewModel() {
    fun fetchAdvice(): Flow<ApiResult<String>> = flow {
        try {
            val response = apiService.fetchAdvice()
            if (response.isSuccessful) {
                val advice = response.body()?.slips?.get(0)?.advice
                if (advice == null){
                    emit(ApiResult.Error("NULL!", response.code()))
                    return@flow
                }
                emit(ApiResult.Success(advice))
            }
        } catch (e: java.lang.Exception) {
            emit(ApiResult.Error(e.localizedMessage ?: "An error occurred", 0))
        }
    }
}
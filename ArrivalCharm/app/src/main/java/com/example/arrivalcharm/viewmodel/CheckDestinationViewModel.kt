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
class CheckDestinationViewModel @Inject constructor(
    @NetworkModule.Main private val apiService: ApiService
) : ViewModel() {
    fun getDestinationList(header: String): Flow<ApiResult<String>> = flow {
        try {
            val field : HashMap<String, String> = HashMap()
            field["Authorization"] = header
            val response = apiService.getDestinationList(field)
            if (response.isSuccessful) {
                emit(ApiResult.Success("SUCCESS!!"))
            } else {
                emit(ApiResult.Error("Failed...", response.code()))
            }
        } catch (e: java.lang.Exception) {
            emit(ApiResult.Error(e.localizedMessage ?: "An error occurred", 0))
        }
    }
}
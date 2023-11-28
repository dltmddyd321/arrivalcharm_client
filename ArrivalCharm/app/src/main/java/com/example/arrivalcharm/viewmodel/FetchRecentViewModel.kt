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
class FetchRecentViewModel @Inject constructor(
    @NetworkModule.Main private val apiService: ApiService
) : ViewModel() {
    fun fetchRecentSearch(header: String): Flow<ApiResult<Boolean>> = flow {
        try {
            val field: HashMap<String, String> = HashMap()
            field["Authorization"] = header
            val response = apiService.getRecentList(field)
            if (response.isSuccessful) {
                emit(ApiResult.Success(true))
            } else {
                emit(ApiResult.Error("Failed...", response.code()))
            }
        } catch (e: Exception) {
            emit(ApiResult.Error(e.localizedMessage ?: "An error occurred", 0))
        }
    }
}
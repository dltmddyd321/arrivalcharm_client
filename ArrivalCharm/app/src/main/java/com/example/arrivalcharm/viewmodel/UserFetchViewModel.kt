package com.example.arrivalcharm.viewmodel

import androidx.lifecycle.ViewModel
import com.example.arrivalcharm.api.ApiResult
import com.example.arrivalcharm.api.ApiService
import com.example.arrivalcharm.api.NetworkModule
import com.example.arrivalcharm.datamodel.UserCheckData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class UserFetchViewModel @Inject constructor(
    @NetworkModule.Main private val apiService: ApiService
) : ViewModel() {
    fun fetchUserData(header: String, userId: Int): Flow<ApiResult<UserCheckData>> = flow{
        try {
            val field : HashMap<String, String> = HashMap()
            field["Authorization"] = header
            val response = apiService.fetchUser(field, id = userId)
            val result = response.body() ?: run {
                emit(ApiResult.Error("Data is Null", response.code()))
                return@flow
            }
            emit(ApiResult.Success(result))
        } catch (e: Exception) {
            emit(ApiResult.Error(e.localizedMessage ?: "An error occurred", 0))
        }
    }
}
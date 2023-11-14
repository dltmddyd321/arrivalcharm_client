package com.example.arrivalcharm.viewmodel

import androidx.lifecycle.ViewModel
import com.example.arrivalcharm.api.ApiResult
import com.example.arrivalcharm.api.ApiService
import com.example.arrivalcharm.api.NetworkModule
import com.example.arrivalcharm.datamodel.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class UserUpdateViewModel @Inject constructor(
    @NetworkModule.Main private val apiService: ApiService
) : ViewModel() {
    fun updateUserName(header: String, userId: Int, name: String): Flow<ApiResult<Boolean>> = flow {
        try {
            val field : HashMap<String, String> = HashMap()
            field["Authorization"] = header
            val response = apiService.editUserInfo(field, id = userId, displayUsername = name)
            if (response.isSuccessful) {
                emit(ApiResult.Success(true))
            } else {
                emit(ApiResult.Error("Failed!", response.code()))
            }
        } catch (e: Exception) {
            emit(ApiResult.Error(e.localizedMessage ?: "An error occurred", 0))
        }
    }
}
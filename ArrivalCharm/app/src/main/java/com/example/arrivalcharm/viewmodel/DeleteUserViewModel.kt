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
class DeleteUserViewModel @Inject constructor(
    @NetworkModule.Main private val apiService: ApiService
) : ViewModel() {
    fun deleteUser(header: String, id: Int): Flow<ApiResult<Boolean>> = flow {
        try {
            val field: HashMap<String, String> = HashMap()
            field["Authorization"] = header
            val response = apiService.deleteUser(field, id)
            emit(
                if (response.isSuccessful) ApiResult.Success(true)
                else ApiResult.Error("Failed!", response.code())
            )
        } catch (e: Exception) {
            emit(ApiResult.Error(e.localizedMessage ?: "An error occurred", 0))
        }
    }
}
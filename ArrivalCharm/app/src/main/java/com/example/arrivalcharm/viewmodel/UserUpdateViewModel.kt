package com.example.arrivalcharm.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.arrivalcharm.api.ApiResult
import com.example.arrivalcharm.api.ApiService
import com.example.arrivalcharm.api.NetworkModule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UserUpdateViewModel @Inject constructor(
    @NetworkModule.Main private val apiService: ApiService
) : ViewModel() {
    fun updateUserName(header: String, userId: Int, name: String): Flow<ApiResult<UserUpdateResult>> = flow {
        try {
            val field : HashMap<String, String> = HashMap()
            field["Authorization"] = header
            val response = apiService.editUserInfo(field, id = userId, displayUsername = name)
            emit(ApiResult.Success(UserUpdateResult(response.isSuccessful, response.code())))
        } catch (e: Exception) {
            emit(ApiResult.Error(e.localizedMessage ?: "An error occurred", 0))
        }
    }
    fun updateUserProfileImg(header: String, userId: Int, imageFile: File): Flow<ApiResult<UserUpdateResult>> = flow {
        try {
            val imageRequestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, imageRequestBody)
            val field : HashMap<String, String> = HashMap()
            field["Authorization"] = header
            val response = apiService.editUserImage(field, id = userId, file = imagePart)
            emit(ApiResult.Success(UserUpdateResult(response.isSuccessful, response.code())))
        } catch (e: java.lang.Exception) {
            emit(ApiResult.Error(e.localizedMessage ?: "An error occurred", 0))
        }
    }
}

data class UserUpdateResult(
    val isSuccess: Boolean,
    val responseCode: Int
)
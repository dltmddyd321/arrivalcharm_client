package com.example.arrivalcharm.viewmodel

import androidx.lifecycle.ViewModel
import com.example.arrivalcharm.api.ApiResult
import com.example.arrivalcharm.api.ApiService
import com.example.arrivalcharm.api.NetworkModule
import com.example.arrivalcharm.datamodel.LoginResultModel
import com.example.arrivalcharm.datamodel.UserLoginInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    @NetworkModule.Main private val apiService: ApiService
) : ViewModel() {
    fun startLogin(oAuth2UserInfo: UserLoginInfo): Flow<ApiResult<LoginResultModel>> = flow {
        try {
            val response = apiService.requestLogin(oAuth2UserInfo)
            if (response.isSuccessful) {
                val loginResult = response.body()
                if (loginResult != null) {
                    emit(ApiResult.Success(loginResult))
                } else {
                    emit(ApiResult.Error("Result is null", response.code()))
                }
            } else {
                emit(ApiResult.Error("An error occurred", response.code()))
            }
        } catch (e: java.lang.Exception) {
            emit(ApiResult.Error(e.localizedMessage ?: "An error occurred", 0))
        }
    }
}
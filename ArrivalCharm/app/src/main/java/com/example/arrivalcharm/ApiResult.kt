package com.example.arrivalcharm

sealed class ApiResult<out T> {

    object Loading : ApiResult<Nothing>()

    data class Success<out T>(val data: T) : ApiResult<T>()

    sealed class Fail : ApiResult<Nothing>() {
        data class Error(val code: Int, val msg: String?) : Fail()
        data class Exception(val e: Throwable) : Fail()
    }
}
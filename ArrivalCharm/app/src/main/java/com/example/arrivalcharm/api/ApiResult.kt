package com.example.arrivalcharm.api

sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val code: Int) : ApiResult<Nothing>()
}
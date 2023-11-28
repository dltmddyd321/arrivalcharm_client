package com.example.arrivalcharm.api

import com.example.arrivalcharm.db.datastore.DatastoreViewModel
import com.example.arrivalcharm.viewmodel.TokenRefreshViewModel
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    @NetworkModule.Main private val tokenRefreshViewModel: TokenRefreshViewModel,
    private val datastoreViewModel: DatastoreViewModel
): Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code != 500) return null
        val token = runBlocking { datastoreViewModel.getAuthToken() }
        val refreshResult = runBlocking {
            tokenRefreshViewModel.refreshToken(header = token)
        }
        val newRefreshToken = refreshResult?.refreshToken
        val newAccessToken = refreshResult?.accessToken
        if (!newRefreshToken.isNullOrEmpty()) {
            datastoreViewModel.putRefreshToken(newRefreshToken)
        }
        if (!newAccessToken.isNullOrEmpty()) {
            datastoreViewModel.putAuthToken(newAccessToken)
        }
        return response.request.newBuilder()
            .header("Authorization", "Bearer $newAccessToken")
            .build()
    }
}
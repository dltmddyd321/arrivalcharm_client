package com.example.arrivalcharm.api

import androidx.lifecycle.viewModelScope
import com.example.arrivalcharm.db.datastore.DatastoreViewModel
import com.example.arrivalcharm.viewmodel.TokenRefreshViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val tokenRefreshViewModel: TokenRefreshViewModel,
    private val datastoreViewModel: DatastoreViewModel
): Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        tokenRefreshViewModel.refreshToken(header = token)
    }
}
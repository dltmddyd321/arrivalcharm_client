package com.example.arrivalcharm.util

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.impl.model.Dependency
import com.example.arrivalcharm.api.ApiResult
import com.example.arrivalcharm.api.NetworkModule
import com.example.arrivalcharm.db.datastore.DatastoreViewModel
import com.example.arrivalcharm.viewmodel.TokenRefreshViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltWorker
class RefreshWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    @NetworkModule.Main private val tokenRefreshViewModel: TokenRefreshViewModel,
    private val dataStoreViewModel: DatastoreViewModel
) : Worker(context, params) {
    override fun doWork(): Result {
        CoroutineScope(Dispatchers.IO).launch {
            val refreshToken = dataStoreViewModel.getRefreshToken()
            tokenRefreshViewModel.refreshToken(refreshToken).collect {
                when (it) {
                    is ApiResult.Success -> {
                        val newRefreshToken = it.data?.refreshToken
                        val newAccessToken = it.data?.accessToken
                        if (!newRefreshToken.isNullOrEmpty()) dataStoreViewModel.putRefreshToken(newRefreshToken)
                        if (!newAccessToken.isNullOrEmpty()) dataStoreViewModel.putAuthToken(newAccessToken)
                    }
                    else -> Timber.d("토큰 갱신 실패!")
                }
            }
        }
        return Result.success()
    }
}
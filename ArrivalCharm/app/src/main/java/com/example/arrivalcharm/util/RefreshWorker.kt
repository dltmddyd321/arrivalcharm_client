package com.example.arrivalcharm.util

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.work.CoroutineWorker
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
    private val viewModelFactory: ViewModelProvider.Factory
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        val datastoreViewModel = ViewModelProvider(
            ViewModelStore(),
            viewModelFactory
        )[DatastoreViewModel::class.java]

        val refreshToken = datastoreViewModel.getRefreshToken()

        return Result.success()
    }
}
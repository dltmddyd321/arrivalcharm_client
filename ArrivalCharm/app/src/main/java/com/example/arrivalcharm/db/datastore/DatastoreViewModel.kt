package com.example.arrivalcharm.db.datastore

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DatastoreViewModel @Inject constructor(
    private val datastoreRepo: DatastoreRepo
) : ViewModel() {

    suspend fun isValidLogin(): Boolean = withContext(Dispatchers.IO) {
        !datastoreRepo.getString("AUTH_TOKEN").isNullOrEmpty()
    }

    fun putAuthToken(value: String) {
        CoroutineScope(Dispatchers.IO).launch {
            datastoreRepo.putString("AUTH_TOKEN", value)
        }
    }

    fun putAuthId(value: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            datastoreRepo.putString("AUTH_ID", value.toString())
        }
    }

    suspend fun getAuthToken(): String = withContext(Dispatchers.IO) {
        ("Bearer " + datastoreRepo.getString("AUTH_TOKEN"))
    }

    suspend fun getAuthId(): Int = withContext(Dispatchers.IO) {
        datastoreRepo.getString("AUTH_ID")?.toInt() ?: -1
    }

    fun putRefreshToken(value: String) {
        CoroutineScope(Dispatchers.IO).launch {
            datastoreRepo.putString("REFRESH_TOKEN", value)
        }
    }

    suspend fun getRefreshToken(): String = datastoreRepo.getString("REFRESH_TOKEN") ?: ""
}
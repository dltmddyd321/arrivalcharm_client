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

    fun putAuthToken(value: String) {
        CoroutineScope(Dispatchers.IO).launch {
            datastoreRepo.putString("AUTH_TOKEN", value)
        }
    }

    suspend fun getAuthToken(): String = withContext(Dispatchers.IO) {
        datastoreRepo.getString("AUTH_TOKEN") ?: ""
    }
}
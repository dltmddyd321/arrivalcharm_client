package com.example.arrivalcharm.db.datastore

interface DatastoreRepo {
    suspend fun putString(key: String, value: String)
    suspend fun putBoolean(key: String, value: Boolean)
    suspend fun getString(key: String): String?
    suspend fun clearPrefs(key: String)
}
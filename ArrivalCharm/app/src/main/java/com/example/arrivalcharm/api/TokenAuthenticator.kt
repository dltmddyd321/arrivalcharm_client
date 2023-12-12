package com.example.arrivalcharm.api

import android.content.Context
import androidx.activity.viewModels
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.arrivalcharm.datamodel.TokenRefreshResult
import com.example.arrivalcharm.db.datastore.DatastoreViewModel
import com.example.arrivalcharm.db.datastore.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    @ApplicationContext val context: Context
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code != 500) return null

        val refreshToken = runBlocking { getString("REFRESH_TOKEN") }
        if (refreshToken.isNullOrEmpty()) return null

        val refreshResult = getRefreshRetrofit<TokenRefreshApi>().getRefreshToken(refreshToken).execute()
        refreshResult.body()?.let {
            runBlocking {
                putString("REFRESH_TOKEN", it.refreshToken)
                putString("AUTH_TOKEN", it.accessToken)
            }
            return response.request.newBuilder()
                .header("Authorization", "Bearer ${it.accessToken}")
                .build()
        }
        return null
    }

    private suspend fun getString(key: String): String? {
        return try {
            val prefsKey = stringPreferencesKey(key)
            val prefs = context.dataStore.data.first()
            prefs[prefsKey]
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun putString(key: String, value: String) {
        val prefsKey = stringPreferencesKey(key)
        context.dataStore.edit {
            it[prefsKey] = value
        }
    }

    private val refreshClient = OkHttpClient.Builder()
        .connectTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private inline fun <reified T> getRefreshRetrofit(): T {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://210.103.99.38:8080")
            .client(refreshClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(T::class.java)
    }

    interface TokenRefreshApi {
        @POST("/api/v1/auth/accessToken")
        fun getRefreshToken(
            @Body refreshToken: String
        ): retrofit2.Call<TokenRefreshResult>
    }
}
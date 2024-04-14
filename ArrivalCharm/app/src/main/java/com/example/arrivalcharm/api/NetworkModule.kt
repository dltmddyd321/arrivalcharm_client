package com.example.arrivalcharm.api

import android.content.Context
import com.example.arrivalcharm.BuildConfig
import com.example.arrivalcharm.db.datastore.DatastoreViewModel
import com.example.arrivalcharm.viewmodel.TokenRefreshViewModel
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class Main

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class Advice

    @Named("mainBaseUrl")
    @Provides
    fun provideBaseUrl() = "http://121.165.115.41:8081"
    //121.165.115.41:8081 -> Sub Main
    //210.103.99.38:8080 -> Main

    @Named("adviceBaseUrl")
    @Provides
    fun adviceBaseUrl() = "https://api.adviceslip.com"

    @Singleton
    @Provides
    fun provideTokenAuthenticator(@ApplicationContext context: Context): TokenAuthenticator {
        return TokenAuthenticator(context)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(tokenAuthenticator: TokenAuthenticator): OkHttpClient {
        val connectionTimeOut = (1000 * 30).toLong()
        val readTimeOut = (1000 * 5).toLong()

        val interceptor = HttpLoggingInterceptor()

        HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                if (!message.startsWith("{") && !message.startsWith("[")) {
                    Timber.tag("OkHttp").d(message)
                    return
                }
                try {
                    Timber.tag("OkHttp").d(
                        GsonBuilder().setPrettyPrinting().create().toJson(
                            JsonParser().parse(message)
                        )
                    )
                } catch (m: JsonSyntaxException) {
                    Timber.tag("OkHttp").d(message)
                }
            }
        })

        interceptor.level = HttpLoggingInterceptor.Level.NONE

        if (BuildConfig.DEBUG) {
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .readTimeout(readTimeOut, TimeUnit.MILLISECONDS)
            .connectTimeout(connectionTimeOut, TimeUnit.MILLISECONDS)
            .authenticator(tokenAuthenticator)
            .addInterceptor(interceptor)
            .build()
    }

    @Singleton
    @Provides
    @Main
    fun provideMainRetrofit(
        okHttpClient: OkHttpClient,
        @Named("mainBaseUrl") baseUrl: String
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    @Advice
    fun provideAdviceRetrofit(
        okHttpClient: OkHttpClient,
        @Named("adviceBaseUrl") baseUrl: String
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Main
    fun provideMainApiService(@Main retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    @Advice
    fun provideAdviceApiService(@Advice retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
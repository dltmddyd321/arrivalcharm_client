package com.example.arrivalcharm.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DBModule {

    @Singleton
    @Provides
    fun provideLocationDatabase(
        @ApplicationContext context: Context
    ): LocationDatabase = Room.databaseBuilder(context, LocationDatabase::class.java, "location.db").build()

    @Singleton
    @Provides
    fun provideLocationDao(locationDatabase: LocationDatabase): LocationDao = locationDatabase.locationDao()
}
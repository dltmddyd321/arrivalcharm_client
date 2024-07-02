package com.example.arrivalcharm.db

import android.content.Context
import androidx.room.Room
import com.example.arrivalcharm.db.datastore.DatastoreRepo
import com.example.arrivalcharm.db.datastore.DatastoreRepoImpl
import com.example.arrivalcharm.db.room.LocationDao
import com.example.arrivalcharm.db.room.LocationDatabase
import com.example.arrivalcharm.db.room.RecentDao
import com.example.arrivalcharm.db.room.RecentDatabase
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
    fun providesDatastoreRepo(
        @ApplicationContext context: Context
    ): DatastoreRepo = DatastoreRepoImpl(context)

    @Singleton
    @Provides
    fun provideLocationDatabase(
        @ApplicationContext context: Context
    ): LocationDatabase = Room.databaseBuilder(context, LocationDatabase::class.java, "location.db").build()

    @Singleton
    @Provides
    fun provideLocationDao(locationDatabase: LocationDatabase): LocationDao = locationDatabase.locationDao()

    @Singleton
    @Provides
    fun provideRecentDatabase(
        @ApplicationContext context: Context
    ): RecentDatabase = Room.databaseBuilder(context, RecentDatabase::class.java, "recent.db").build()

    @Singleton
    @Provides
    fun provideRecentDao(recentDatabase: RecentDatabase): RecentDao = recentDatabase.recentDao()
}
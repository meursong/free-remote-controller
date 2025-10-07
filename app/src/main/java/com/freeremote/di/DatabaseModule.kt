package com.freeremote.di

import android.content.Context
import androidx.room.Room
import com.freeremote.data.database.RemoteDatabase
import com.freeremote.data.database.dao.DeviceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideRemoteDatabase(
        @ApplicationContext context: Context
    ): RemoteDatabase {
        return Room.databaseBuilder(
            context,
            RemoteDatabase::class.java,
            "remote_database"
        ).build()
    }

    @Provides
    fun provideDeviceDao(database: RemoteDatabase): DeviceDao {
        return database.deviceDao()
    }
}
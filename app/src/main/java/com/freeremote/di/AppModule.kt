package com.freeremote.di

import android.content.Context
import com.freeremote.domain.manager.CastManager
import com.freeremote.domain.manager.DialManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for application-level dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCastManager(
        @ApplicationContext context: Context
    ): CastManager {
        return CastManager(context)
    }

    @Provides
    @Singleton
    fun provideDialManager(
        @ApplicationContext context: Context
    ): DialManager {
        return DialManager(context)
    }
}
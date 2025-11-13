package com.igarza.debuglogger.di

import android.content.Context
import com.igarza.debuglogger.data.notification.LogNotificationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

    @Provides
    @Singleton
    fun provideLogNotificationManager(
        @ApplicationContext context: Context
    ): LogNotificationManager {
        return LogNotificationManager(context)
    }
}
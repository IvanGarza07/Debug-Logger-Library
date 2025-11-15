package com.igarza.debuglogger.di

import androidx.hilt.work.HiltWorkerFactory
import com.igarza.debuglogger.data.logger.DebugLogger
import com.igarza.debuglogger.data.logger.LoggerConfigProvider
import com.igarza.debuglogger.data.notification.LogNotificationManager
import com.igarza.debuglogger.domain.model.LoggerConfig
import com.igarza.debuglogger.domain.repository.LogRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LoggerModule {

    @Provides
    @Singleton
    fun provideLoggerConfigProvider(): LoggerConfigProvider {
        return LoggerConfigProvider()
    }

    @Provides
    @Singleton
    fun provideLoggerConfig(
        configProvider: LoggerConfigProvider
    ): LoggerConfig {
        return configProvider.getConfig()
    }

    @Provides
    @Singleton
    fun provideDebugLogger(
        repository: LogRepository,
        notificationManager: LogNotificationManager,
        config: LoggerConfig
    ): DebugLogger {
        return DebugLogger(repository, notificationManager, config)
    }
}
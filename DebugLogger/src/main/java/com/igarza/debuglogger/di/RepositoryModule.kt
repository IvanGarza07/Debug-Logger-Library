package com.igarza.debuglogger.di

import com.igarza.debuglogger.data.database.LogDao
import com.igarza.debuglogger.data.repository.LogRepositoryImpl
import com.igarza.debuglogger.domain.repository.LogRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideLogRepository(
        logDao: LogDao
    ): LogRepository {
        return LogRepositoryImpl(logDao)
    }
}
package com.igarza.debuglogger.di

import android.content.Context
import androidx.room.Room
import com.igarza.debuglogger.data.database.LogDao
import com.igarza.debuglogger.data.database.LogDatabase
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
    fun provideLogDatabase(
        @ApplicationContext context: Context
    ): LogDatabase {
        return Room.databaseBuilder(
            context,
            LogDatabase::class.java,
            LogDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    @Singleton
    fun provideLogDao(database: LogDatabase): LogDao {
        return database.logDao()
    }
}
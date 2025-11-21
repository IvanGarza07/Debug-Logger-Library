package com.igarza.debuglogger.di

import android.content.Context
import androidx.room.Room
import com.igarza.debuglogger.data.database.LogDatabase
import com.igarza.debuglogger.data.logger.DebugLogger
import com.igarza.debuglogger.data.notification.LogNotificationManager
import com.igarza.debuglogger.data.repository.LogRepositoryImpl
import com.igarza.debuglogger.domain.model.LoggerConfig
import com.igarza.debuglogger.domain.repository.LogRepository

/**
 * Internal singleton manager for the logger components.
 * This replaces Hilt's dependency injection with manual singleton management.
 */
internal object LoggerInstance {

    @Volatile
    private var appContext: Context? = null

    @Volatile
    private var config: LoggerConfig? = null

    fun initialize(context: Context, loggerConfig: LoggerConfig) {
        if (appContext == null) {
            appContext = context.applicationContext
        }
        config = loggerConfig
    }

    val database: LogDatabase by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        Room.databaseBuilder(
            getAppContext(),
            LogDatabase::class.java,
            LogDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }

    val repository: LogRepository by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        LogRepositoryImpl(database.logDao())
    }

    val notificationManager: LogNotificationManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        LogNotificationManager(getAppContext())
    }

    val debugLogger: DebugLogger by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        DebugLogger(
            repository,
            notificationManager,
            config ?: LoggerConfig()
        )
    }

    fun getConfig(): LoggerConfig = config ?: LoggerConfig()

    fun isInitialized(): Boolean = appContext != null

    private fun getAppContext(): Context {
        return appContext
            ?: throw IllegalStateException("LoggerInstance not initialized.")
    }
}
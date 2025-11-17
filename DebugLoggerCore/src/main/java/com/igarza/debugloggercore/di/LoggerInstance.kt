package com.igarza.debugloggercore.di

import android.content.Context
import androidx.room.Room
import com.igarza.debugloggercore.data.database.LogDatabase
import com.igarza.debugloggercore.data.logger.DebugLogger
import com.igarza.debugloggercore.data.notification.LogNotificationManager
import com.igarza.debugloggercore.data.repository.LogRepositoryImpl
import com.igarza.debugloggercore.domain.model.LoggerConfig
import com.igarza.debugloggercore.domain.repository.LogRepository

/**
 * Internal singleton manager for the logger components.
 * This replaces Hilt's dependency injection with manual singleton management.
 */
internal object LoggerInstance {

    @Volatile
    private var database: LogDatabase? = null

    @Volatile
    private var repository: LogRepository? = null

    @Volatile
    private var notificationManager: LogNotificationManager? = null

    @Volatile
    private var debugLogger: DebugLogger? = null

    @Volatile
    private var config: LoggerConfig? = null

    @Volatile
    private var appContext: Context? = null

    fun initialize(context: Context, loggerConfig: LoggerConfig) {
        if (appContext != null) {
            // Already initialized, just update config
            config = loggerConfig
            return
        }

        appContext = context.applicationContext
        config = loggerConfig

        // Initialize components
        getDatabase(context.applicationContext)
        getRepository(context.applicationContext)
        getNotificationManager(context.applicationContext)
        getDebugLogger(context.applicationContext)
    }

    fun getDatabase(context: Context): LogDatabase {
        return database ?: synchronized(this) {
            database ?: Room.databaseBuilder(
                context.applicationContext,
                LogDatabase::class.java,
                LogDatabase.DATABASE_NAME
            )
                .fallbackToDestructiveMigration(false)
                .build()
                .also { database = it }
        }
    }

    fun getRepository(context: Context): LogRepository {
        return repository ?: synchronized(this) {
            repository ?: LogRepositoryImpl(
                getDatabase(context).logDao()
            ).also { repository = it }
        }
    }

    fun getNotificationManager(context: Context): LogNotificationManager {
        return notificationManager ?: synchronized(this) {
            notificationManager ?: LogNotificationManager(
                context.applicationContext
            ).also { notificationManager = it }
        }
    }

    fun getConfig(): LoggerConfig {
        return config ?: LoggerConfig()
    }

    fun getDebugLogger(context: Context): DebugLogger {
        return debugLogger ?: synchronized(this) {
            debugLogger ?: DebugLogger(
                getRepository(context),
                getNotificationManager(context),
                LoggerConfig(
                    showNotification = true,
                    maxLogs = 10000
                )
            ).also { debugLogger = it }
        }
    }

    fun isInitialized(): Boolean {
        return appContext != null
    }

    fun getContext(): Context {
        return appContext ?: throw IllegalStateException(
            "LoggerInstance not initialized. Call DebugLogger.install() first."
        )
    }
}
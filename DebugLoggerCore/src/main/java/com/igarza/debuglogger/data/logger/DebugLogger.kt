package com.igarza.debuglogger.data.logger

import android.content.Context
import android.util.Log
import com.igarza.debuglogger.data.notification.LogNotificationManager
import com.igarza.debuglogger.di.LoggerInstance
import com.igarza.debuglogger.domain.enums.LogLevel
import com.igarza.debuglogger.domain.model.LogEntry
import com.igarza.debuglogger.domain.model.LoggerConfig
import com.igarza.debuglogger.domain.repository.LogRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DebugLogger(
    private val repository: LogRepository,
    private val notificationManager: LogNotificationManager,
    private val config: LoggerConfig
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val timberAvailable = isTimberAvailable()

    companion object {
        @Volatile
        private var instance: DebugLogger? = null

        /**
         * Install and configure DebugLogger.
         * This should be called in Application.onCreate()
         *
         * @param context Application context
         * @param config Logger configuration
         */
        fun install(context: Context, config: LoggerConfig = LoggerConfig()) {
            LoggerInstance.initialize(context, config)
            instance = LoggerInstance.debugLogger
        }

        /**
         * Get the singleton instance.
         * Must be installed first via install()
         */
        fun getInstance(): DebugLogger {
            return instance ?: LoggerInstance.debugLogger.also { instance = it }
        }

        // Convenience static methods
        @JvmStatic
        fun v(tag: String, message: String) {
            getInstance().verbose(tag, message)
        }

        @JvmStatic
        fun d(tag: String, message: String) {
            getInstance().debug(tag, message)
        }

        @JvmStatic
        fun i(tag: String, message: String) {
            getInstance().info(tag, message)
        }

        @JvmStatic
        fun w(tag: String, message: String) {
            getInstance().warn(tag, message)
        }

        @JvmStatic
        fun e(tag: String, message: String, throwable: Throwable? = null) {
            getInstance().error(tag, message, throwable)
        }

        @JvmStatic
        fun a(tag: String, message: String, throwable: Throwable? = null) {
            getInstance().assert(tag, message, throwable)
        }

        /**
         * Clear all logs and hide notification
         */
        @JvmStatic
        fun clearAll() {
            getInstance().clearLogs()
        }
    }

    private fun verbose(tag: String, message: String) {
        log(LogLevel.VERBOSE, tag, message, null)
    }

    private fun debug(tag: String, message: String) {
        log(LogLevel.DEBUG, tag, message, null)
    }

    private fun info(tag: String, message: String) {
        log(LogLevel.INFO, tag, message, null)
    }

    private fun warn(tag: String, message: String) {
        log(LogLevel.WARN, tag, message, null)
    }

    private fun error(tag: String, message: String, throwable: Throwable? = null) {
        log(LogLevel.ERROR, tag, message, throwable)
    }

    private fun assert(tag: String, message: String, throwable: Throwable? = null) {
        log(LogLevel.WTF, tag, message, throwable)
    }

    private fun clearLogs() {
        scope.launch {
            repository.clearAllLogs()
            notificationManager.hideNotification()
        }
    }

    private fun log(level: LogLevel, tag: String, message: String, throwable: Throwable?) {
        // Log to Logcat (via Timber if available)
        logToConsole(level, tag, message, throwable)

        // Store in database
        if (config.enablePersistence) {
            scope.launch {
                val logEntry = LogEntry(
                    level = level,
                    tag = tag,
                    message = message,
                    throwable = throwable?.stackTraceToString()
                )

                repository.insertLog(logEntry)

                // Maintain max logs limit
                if (config.maxLogs > 0) {
                    repository.maintainMaxLogs(config.maxLogs)
                }

                // Update notification
                if (config.showNotification) {
                    val latestLogs = repository.getLatestLogs(5).first()
                    notificationManager.showNotification(latestLogs)
                }
            }
        }
    }

    private fun logToConsole(level: LogLevel, tag: String, message: String, throwable: Throwable?) {
        if (timberAvailable) {
            logViaTimber(level, tag, message, throwable)
        } else {
            logViaAndroidLog(level, tag, message, throwable)
        }
    }

    private fun logViaAndroidLog(
        level: LogLevel,
        tag: String,
        message: String,
        throwable: Throwable?
    ) {
        when (level) {
            LogLevel.VERBOSE -> Log.v(tag, message, throwable)
            LogLevel.DEBUG -> Log.d(tag, message, throwable)
            LogLevel.INFO -> Log.i(tag, message, throwable)
            LogLevel.WARN -> Log.w(tag, message, throwable)
            LogLevel.ERROR -> Log.e(tag, message, throwable)
            LogLevel.WTF -> Log.wtf(tag, message, throwable)
        }
    }

    private fun logViaTimber(level: LogLevel, tag: String, message: String, throwable: Throwable?) {
        try {
            val timberClass = Class.forName("timber.log.Timber")
            val tagMethod = timberClass.getMethod("tag", String::class.java)
            val tree = tagMethod.invoke(null, tag)

            val logMethod = when (level) {
                LogLevel.VERBOSE -> tree.javaClass.getMethod(
                    "v",
                    String::class.java,
                    Array<Any>::class.java
                )

                LogLevel.DEBUG -> tree.javaClass.getMethod(
                    "d",
                    String::class.java,
                    Array<Any>::class.java
                )

                LogLevel.INFO -> tree.javaClass.getMethod(
                    "i",
                    String::class.java,
                    Array<Any>::class.java
                )

                LogLevel.WARN -> tree.javaClass.getMethod(
                    "w",
                    String::class.java,
                    Array<Any>::class.java
                )

                LogLevel.ERROR -> tree.javaClass.getMethod(
                    "e",
                    Throwable::class.java,
                    String::class.java,
                    Array<Any>::class.java
                )

                LogLevel.WTF -> tree.javaClass.getMethod(
                    "wtf",
                    Throwable::class.java,
                    String::class.java,
                    Array<Any>::class.java
                )
            }

            if (level == LogLevel.ERROR || level == LogLevel.WTF) {
                logMethod.invoke(tree, throwable, message, emptyArray<Any>())
            } else {
                logMethod.invoke(tree, message, emptyArray<Any>())
            }
        } catch (e: Exception) {
            // Fallback to Android Log
            Log.e("DebugLogger", "Error logging via Timber", e)
            logViaAndroidLog(level, tag, message, throwable)
        }
    }

    private fun isTimberAvailable(): Boolean {
        return try {
            Class.forName("timber.log.Timber")
            true
        } catch (e: ClassNotFoundException) {
            Log.e("DebugLogger", "Timber is not available", e)
            false
        }
    }
}
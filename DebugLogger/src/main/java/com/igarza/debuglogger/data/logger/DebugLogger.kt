package com.igarza.debuglogger.data.logger

import android.annotation.SuppressLint
import android.util.Log
import com.igarza.debuglogger.data.notification.LogNotificationManager
import com.igarza.debuglogger.domain.enums.LogLevel
import com.igarza.debuglogger.domain.model.LogEntry
import com.igarza.debuglogger.domain.model.LoggerConfig
import com.igarza.debuglogger.domain.repository.LogRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DebugLogger @Inject constructor(
    private val repository: LogRepository,
    private val notificationManager: LogNotificationManager,
    private val config: LoggerConfig
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val timberAvailable = isTimberAvailable()

//    init {
//        // Set singleton instance when created by Hilt
//        instance = this
//    }
//
//    companion object {
//        @Volatile
//        private var instance: DebugLogger? = null
//
//        fun getInstance(): DebugLogger {
//            return instance ?: throw IllegalStateException(
//                "DebugLogger must be initialized first. Call DebugLogger.initialize()"
//            )
//        }
//
//        // Convenience methods
//        fun v(tag: String, message: String) = getInstance().verbose(tag, message)
//        fun d(tag: String, message: String) = getInstance().debug(tag, message)
//        fun i(tag: String, message: String) = getInstance().info(tag, message)
//        fun w(tag: String, message: String) = getInstance().warn(tag, message)
//        fun e(tag: String, message: String, throwable: Throwable? = null) =
//            getInstance().error(tag, message, throwable)
//        fun wtf(tag: String, message: String, throwable: Throwable? = null) =
//            getInstance().wtf(tag, message, throwable)
//    }

    fun verbose(tag: String, message: String) {
        log(LogLevel.VERBOSE, tag, message, null)
    }

    fun debug(tag: String, message: String) {
        log(LogLevel.DEBUG, tag, message, null)
    }

    fun info(tag: String, message: String) {
        log(LogLevel.INFO, tag, message, null)
    }

    fun warn(tag: String, message: String) {
        log(LogLevel.WARN, tag, message, null)
    }

    fun error(tag: String, message: String, throwable: Throwable? = null) {
        log(LogLevel.ERROR, tag, message, throwable)
    }

    fun wtf(tag: String, message: String, throwable: Throwable? = null) {
        log(LogLevel.WTF, tag, message, throwable)
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

    @SuppressLint("LogNotTimber")
    private fun logViaAndroidLog(level: LogLevel, tag: String, message: String, throwable: Throwable?) {
        when (level) {
            LogLevel.VERBOSE -> Log.v(tag, message, throwable)
            LogLevel.DEBUG -> Log.d(tag, message, throwable)
            LogLevel.INFO -> Log.i(tag, message, throwable)
            LogLevel.WARN -> Log.w(tag, message, throwable)
            LogLevel.ERROR -> Log.e(tag, message, throwable)
            LogLevel.WTF -> Log.wtf(tag, message, throwable)
        }
    }

    @SuppressLint("LogNotTimber")
    private fun logViaTimber(level: LogLevel, tag: String, message: String, throwable: Throwable?) {
        try {
            val timberClass = Class.forName("timber.log.Timber")
            val tagMethod = timberClass.getMethod("tag", String::class.java)
            val tree = tagMethod.invoke(null, tag)

            val logMethod = when (level) {
                LogLevel.VERBOSE -> tree.javaClass.getMethod("v", String::class.java, Array<Any>::class.java)
                LogLevel.DEBUG -> tree.javaClass.getMethod("d", String::class.java, Array<Any>::class.java)
                LogLevel.INFO -> tree.javaClass.getMethod("i", String::class.java, Array<Any>::class.java)
                LogLevel.WARN -> tree.javaClass.getMethod("w", String::class.java, Array<Any>::class.java)
                LogLevel.ERROR -> tree.javaClass.getMethod("e", Throwable::class.java, String::class.java, Array<Any>::class.java)
                LogLevel.WTF -> tree.javaClass.getMethod("wtf", Throwable::class.java, String::class.java, Array<Any>::class.java)
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

    @SuppressLint("LogNotTimber")
    private fun isTimberAvailable(): Boolean {
        return try {
            Class.forName("timber.log.Timber")
            true
        } catch (e: ClassNotFoundException) {
            Log.e("DebugLogger", "Timber is not available", e)
            false
        }
    }

    fun clearLogs() {
        scope.launch {
            repository.clearAllLogs()
            notificationManager.hideNotification()
        }
    }
}
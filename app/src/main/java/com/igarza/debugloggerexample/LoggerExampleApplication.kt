package com.igarza.debugloggerexample

import android.app.Application
import com.igarza.debuglogger.data.logger.DebugLogger
import com.igarza.debuglogger.domain.model.LoggerConfig

class LoggerExampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        DebugLogger.install(
            context = this,
            config = LoggerConfig(
                showNotification = true,
                maxLogs = 10000
            )
        )
    }
}
package com.igarza.debugloggerexample

import android.app.Application
import com.igarza.debugloggercore.data.logger.DebugLogger
import com.igarza.debugloggercore.domain.model.LoggerConfig

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
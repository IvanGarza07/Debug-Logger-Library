package com.igarza.debugloggerexample

import android.app.Application
import com.igarza.debuglogger.data.logger.DebugLoggerInitializer
import com.igarza.debuglogger.domain.model.LoggerConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LoggerExampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        DebugLoggerInitializer.configure(
            config = LoggerConfig(
                showNotification = true,
                maxLogs = 10000
            )
        )
    }
}
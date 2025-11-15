package com.igarza.debugloggerexample

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.igarza.debuglogger.data.logger.DebugLoggerInitializer
import com.igarza.debuglogger.domain.model.LoggerConfig
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class LoggerExampleApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

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
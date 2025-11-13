package com.igarza.debuglogger.data.logger

import com.igarza.debuglogger.domain.model.LoggerConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoggerConfigProvider @Inject constructor() {

    init {
        DebugLoggerInitializer.configure(LoggerConfig())
    }

    fun getConfig(): LoggerConfig {
        return DebugLoggerInitializer.getConfig()
    }
}
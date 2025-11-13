package com.igarza.debuglogger.data.logger

import com.igarza.debuglogger.domain.model.LoggerConfig

object DebugLoggerInitializer {

    @Volatile
    private var configuredConfig: LoggerConfig? = null

    /**
     * Configure DebugLogger with custom settings.
     * This must be called before any logging operations, ideally in Application.onCreate()
     *
     * @param config Custom logger configuration
     */
    fun configure(config: LoggerConfig) {
        this.configuredConfig = config
    }

    /**
     * Get the configured LoggerConfig or return default if not configured
     */
    internal fun getConfig(): LoggerConfig {
        return configuredConfig ?: LoggerConfig()
    }

    /**
     * Check if the logger has been configured
     */
    internal fun isConfigured(): Boolean {
        return configuredConfig != null
    }
}
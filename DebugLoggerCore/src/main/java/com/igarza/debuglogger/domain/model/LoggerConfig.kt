package com.igarza.debuglogger.domain.model

data class LoggerConfig(
    val showNotification: Boolean = true,
    val enablePersistence: Boolean = true,
    val maxLogs: Int = 5000
)

package com.igarza.debuglogger.domain.model

import com.igarza.debuglogger.domain.enums.LogLevel

data class LogFilter(
    val searchQuery: String = "",
    val selectedLevels: Set<LogLevel> = LogLevel.entries.toSet(),
    val startTime: Long? = null,
    val endTime: Long? = null
)

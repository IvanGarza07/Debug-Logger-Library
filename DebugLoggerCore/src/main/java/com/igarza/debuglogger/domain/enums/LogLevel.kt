package com.igarza.debuglogger.domain.enums

enum class LogLevel(val priority: Int, val displayName: String) {
    VERBOSE(2, "Verbose"),
    DEBUG(3, "Debug"),
    INFO(4, "Info"),
    WARN(5, "Warning"),
    ERROR(6, "Error"),
    WTF(7, "Assert");

    companion object {
        fun fromPriority(priority: Int): LogLevel {
            return entries.firstOrNull { it.priority == priority } ?: DEBUG
        }
    }
}
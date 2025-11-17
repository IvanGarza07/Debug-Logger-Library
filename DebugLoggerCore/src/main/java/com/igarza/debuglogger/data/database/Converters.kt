package com.igarza.debuglogger.data.database

import androidx.room.TypeConverter
import com.igarza.debuglogger.domain.enums.LogLevel

class Converters {

    @TypeConverter
    fun fromLogLevel(level: LogLevel): Int = level.priority

    @TypeConverter
    fun toLogLevel(priority: Int): LogLevel = LogLevel.fromPriority(priority)
}
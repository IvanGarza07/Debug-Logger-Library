package com.igarza.debugloggercore.data.database

import androidx.room.TypeConverter
import com.igarza.debugloggercore.domain.enums.LogLevel

class Converters {

    @TypeConverter
    fun fromLogLevel(level: LogLevel): Int = level.priority

    @TypeConverter
    fun toLogLevel(priority: Int): LogLevel = LogLevel.fromPriority(priority)
}
package com.igarza.debuglogger.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.igarza.debuglogger.domain.enums.LogLevel

@Entity(tableName = "logs")
data class LogEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val level: LogLevel,
    val tag: String,
    val message: String,
    val throwable: String? = null
)

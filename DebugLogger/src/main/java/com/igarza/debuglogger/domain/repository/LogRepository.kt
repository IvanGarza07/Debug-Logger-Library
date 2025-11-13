package com.igarza.debuglogger.domain.repository

import com.igarza.debuglogger.domain.model.LogEntry
import com.igarza.debuglogger.domain.model.LogFilter
import kotlinx.coroutines.flow.Flow

interface LogRepository {
    fun getAllLogs(): Flow<List<LogEntry>>

    fun getLatestLogs(limit: Int): Flow<List<LogEntry>>

    fun getFilteredLogs(filter: LogFilter): Flow<List<LogEntry>>

    suspend fun insertLog(log: LogEntry): Long

    suspend fun clearAllLogs()

    suspend fun deleteLog(id: Long)

    fun getLogCount(): Flow<Int>

    suspend fun maintainMaxLogs(maxLogs: Int)
}
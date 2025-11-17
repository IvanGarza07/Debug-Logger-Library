package com.igarza.debugloggercore.data.repository

import com.igarza.debugloggercore.data.database.LogDao
import com.igarza.debugloggercore.domain.model.LogEntry
import com.igarza.debugloggercore.domain.model.LogFilter
import com.igarza.debugloggercore.domain.repository.LogRepository
import kotlinx.coroutines.flow.Flow

internal class LogRepositoryImpl(
    private val logDao: LogDao
) : LogRepository {

    override fun getAllLogs(): Flow<List<LogEntry>> {
        return logDao.getAllLogs()
    }

    override fun getLatestLogs(limit: Int): Flow<List<LogEntry>> {
        return logDao.getLatestLogs(limit)
    }

    override fun getFilteredLogs(filter: LogFilter): Flow<List<LogEntry>> {
        return logDao.getFilteredLogs(
            query = filter.searchQuery,
            levels = filter.selectedLevels.map { it.priority },
            startTime = filter.startTime,
            endTime = filter.endTime
        )
    }

    override suspend fun insertLog(log: LogEntry): Long {
        return logDao.insertLog(log)
    }

    override suspend fun clearAllLogs() {
        logDao.clearAll()
    }

    override suspend fun deleteLog(id: Long) {
        logDao.deleteLog(id)
    }

    override fun getLogCount(): Flow<Int> {
        return logDao.getLogCount()
    }

    override suspend fun maintainMaxLogs(maxLogs: Int) {
        logDao.deleteOldLogs(maxLogs)
    }
}
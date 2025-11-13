package com.igarza.debuglogger.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.igarza.debuglogger.domain.model.LogEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {

    @Query("SELECT * FROM logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<LogEntry>>

    @Query("SELECT * FROM logs ORDER BY timestamp DESC LIMIT :limit")
    fun getLatestLogs(limit: Int): Flow<List<LogEntry>>

    @Query("""
        SELECT * FROM logs 
        WHERE (:query = '' OR message LIKE '%' || :query || '%' OR tag LIKE '%' || :query || '%')
        AND level IN (:levels)
        AND (:startTime IS NULL OR timestamp >= :startTime)
        AND (:endTime IS NULL OR timestamp <= :endTime)
        ORDER BY timestamp DESC
    """)
    fun getFilteredLogs(
        query: String,
        levels: List<Int>,
        startTime: Long?,
        endTime: Long?
    ): Flow<List<LogEntry>>

    @Insert
    suspend fun insertLog(log: LogEntry): Long

    @Query("DELETE FROM logs")
    suspend fun clearAll()

    @Query("DELETE FROM logs WHERE id = :id")
    suspend fun deleteLog(id: Long)

    @Query("SELECT COUNT(*) FROM logs")
    fun getLogCount(): Flow<Int>

    @Query("DELETE FROM logs WHERE id IN (SELECT id FROM logs ORDER BY timestamp DESC LIMIT -1 OFFSET :maxLogs)")
    suspend fun deleteOldLogs(maxLogs: Int)
}
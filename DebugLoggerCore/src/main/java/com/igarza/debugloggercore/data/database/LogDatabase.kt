package com.igarza.debugloggercore.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.igarza.debugloggercore.domain.model.LogEntry

@Database(entities = [LogEntry::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class LogDatabase : RoomDatabase() {
    abstract fun logDao(): LogDao

    companion object {
        const val DATABASE_NAME = "debug_logger_db"
    }
}
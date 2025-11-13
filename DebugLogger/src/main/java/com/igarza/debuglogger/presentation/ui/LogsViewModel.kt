package com.igarza.debuglogger.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igarza.debuglogger.data.notification.LogNotificationManager
import com.igarza.debuglogger.domain.enums.LogLevel
import com.igarza.debuglogger.domain.model.LogEntry
import com.igarza.debuglogger.domain.model.LogFilter
import com.igarza.debuglogger.domain.repository.LogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogsViewModel @Inject constructor(
    private val repository: LogRepository,
    private val notificationManager: LogNotificationManager
) : ViewModel() {

    private val _filter = MutableStateFlow(LogFilter())
    val filter = _filter.asStateFlow()

    private val _selectedLog = MutableStateFlow<LogEntry?>(null)
    val selectedLog = _selectedLog.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val logs: StateFlow<List<LogEntry>> = filter
        .flatMapLatest { currentFilter ->
            if (currentFilter.searchQuery.isNotEmpty() ||
                currentFilter.selectedLevels.size != LogLevel.entries.size ||
                currentFilter.startTime != null ||
                currentFilter.endTime != null) {
                repository.getFilteredLogs(currentFilter)
            } else {
                repository.getAllLogs()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val logCount = repository.getLogCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    fun updateSearchQuery(query: String) {
        _filter.update { it.copy(searchQuery = query) }
    }

    fun toggleLogLevel(level: LogLevel) {
        _filter.update { currentFilter ->
            val newLevels = if (currentFilter.selectedLevels.contains(level)) {
                currentFilter.selectedLevels - level
            } else {
                currentFilter.selectedLevels + level
            }
            currentFilter.copy(selectedLevels = newLevels)
        }
    }

    fun clearFilter() {
        _filter.value = LogFilter()
    }

    fun selectLog(log: LogEntry?) {
        _selectedLog.value = log
    }

    fun clearAllLogs() {
        viewModelScope.launch {
            repository.clearAllLogs()
            notificationManager.hideNotification()
        }
    }

    fun deleteLog(log: LogEntry) {
        viewModelScope.launch {
            repository.deleteLog(log.id)
            if (_selectedLog.value?.id == log.id) {
                _selectedLog.value = null
            }
        }
    }

    fun exportLogs(): String {
        val currentLogs = logs.value
        return buildString {
            appendLine("=== Debug Logger Export ===")
            appendLine("Total logs: ${currentLogs.size}")
            appendLine("Export time: ${System.currentTimeMillis()}")
            appendLine()

            currentLogs.forEach { log ->
                appendLine("[$${log.level.displayName}] [${log.tag}] ${log.timestamp}")
                appendLine(log.message)
                log.throwable?.let {
                    appendLine("Exception:")
                    appendLine(it)
                }
                appendLine("---")
            }
        }
    }
}
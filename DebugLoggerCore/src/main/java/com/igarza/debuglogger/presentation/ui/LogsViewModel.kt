package com.igarza.debuglogger.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.igarza.debuglogger.data.notification.LogNotificationManager
import com.igarza.debuglogger.di.LoggerInstance
import com.igarza.debuglogger.domain.enums.LogLevel
import com.igarza.debuglogger.domain.model.LogEntry
import com.igarza.debuglogger.domain.model.LogFilter
import com.igarza.debuglogger.domain.repository.LogRepository
import com.igarza.debuglogger.domain.states.LogScreenState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LogsViewModel(
    private val repository: LogRepository,
    private val notificationManager: LogNotificationManager
) : ViewModel() {

    private val _filter = MutableStateFlow(LogFilter())
    val filter = _filter.asStateFlow()

    private val _selectedLog = MutableStateFlow<LogEntry?>(null)
    val selectedLog = _selectedLog.asStateFlow()

    private val _logScreenState = MutableStateFlow(LogScreenState())
    val logScreenState = _logScreenState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val logs: StateFlow<List<LogEntry>> = filter
        .flatMapLatest { currentFilter ->
            if (currentFilter.searchQuery.isNotEmpty() ||
                currentFilter.selectedLevels.size != LogLevel.entries.size ||
                currentFilter.startTime != null ||
                currentFilter.endTime != null
            ) {
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

    init {
        _logScreenState.value = _logScreenState.value.copy(
            onLogClick = ::selectLog,
            onLogDelete = ::deleteLog,
            onSearchChange = ::updateSearchQuery,
            onToggleLevel = ::toggleLogLevel,
            onShowFilterSheet = ::showFilterModal,
            onShowClearDialog = ::showClearDialog,
            onClearFilter = ::clearFilter,
            onConfirmClear = ::clearAllLogs,
            onDismissDetailModal = ::unSelectLog,
            onShowDeleteDialog = ::showDeleteDialog,
            onDeleteLog = ::deleteLogFromBT
        )
    }

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
            showClearDialog(false)
        }
    }

    fun deleteLog(log: LogEntry) {
        viewModelScope.launch {
            repository.deleteLog(log.id)
            if (_selectedLog.value?.id == log.id) {
                unSelectLog()
            }
        }
    }

    fun unSelectLog() {
        _selectedLog.value = null
    }

    fun deleteLogFromBT(log: LogEntry) {
        deleteLog(log)
        selectLog(null)
        showDeleteDialog(false)
    }

    fun showFilterModal(show: Boolean) {
        _logScreenState.value = _logScreenState.value.copy(
            showFilterSheet = show
        )
    }

    fun showClearDialog(show: Boolean) {
        _logScreenState.value = _logScreenState.value.copy(
            showClearDialog = show
        )
    }

    fun showDeleteDialog(show: Boolean) {
        _logScreenState.value = _logScreenState.value.copy(
            showDeleteDialog = show
        )
    }

    companion object {
        fun provideFactory(): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(LogsViewModel::class.java)) {
                        return LogsViewModel(
                            LoggerInstance.repository,
                            LoggerInstance.notificationManager
                        ) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }

}
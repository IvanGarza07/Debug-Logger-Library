package com.igarza.debuglogger.domain.states

import androidx.compose.runtime.Stable
import com.igarza.debuglogger.domain.enums.LogLevel
import com.igarza.debuglogger.domain.model.LogEntry

@Stable
data class LogScreenState(
    // Vars
    val showFilterSheet: Boolean = false,
    val showClearDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    // Events
    val onLogClick: (LogEntry) -> Unit = {},
    val onLogDelete: (LogEntry) -> Unit = {},
    val onSearchChange: (String) -> Unit = {},
    val onToggleLevel: (LogLevel) -> Unit = {},
    val onShowFilterSheet: (show: Boolean) -> Unit = {},
    val onShowClearDialog: (show: Boolean) -> Unit = {},
    val onClearFilter: () -> Unit = {},
    val onConfirmClear: () -> Unit = {},
    val onDismissDetailModal: () -> Unit = {},
    val onShowDeleteDialog: (show: Boolean) -> Unit = {},
    val onDeleteLog: (LogEntry) -> Unit = {}
)

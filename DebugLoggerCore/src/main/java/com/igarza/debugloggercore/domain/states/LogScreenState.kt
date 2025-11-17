package com.igarza.debugloggercore.domain.states

import androidx.compose.runtime.Stable
import com.igarza.debugloggercore.domain.enums.LogLevel
import com.igarza.debugloggercore.domain.model.LogEntry

@Stable
data class LogScreenState(
    val onLogClick: (LogEntry) -> Unit = {},
    val onLogDelete: (LogEntry) -> Unit = {},
    val onSearchChange: (String) -> Unit = {},
    val onToggleLevel: (LogLevel) -> Unit = {},
    val onClearFilter: () -> Unit = {},
    val onConfirmClear: () -> Unit = {},
    val onDismissDelete: () -> Unit = {},
    val onDelete: (LogEntry) -> Unit = {}
)

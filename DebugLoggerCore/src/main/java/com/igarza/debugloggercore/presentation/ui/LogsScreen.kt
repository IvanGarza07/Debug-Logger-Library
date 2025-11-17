package com.igarza.debugloggercore.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.igarza.debugloggercore.presentation.components.LogsScreenContent

@Composable
fun LogsScreen(
    viewModel: LogsViewModel
) {
    val logs by viewModel.logs.collectAsStateWithLifecycle()
    val logCount by viewModel.logCount.collectAsStateWithLifecycle()
    val filter by viewModel.filter.collectAsStateWithLifecycle()
    val selectedLog by viewModel.selectedLog.collectAsStateWithLifecycle()
    val logScreenState by viewModel.logScreenState.collectAsStateWithLifecycle()

    LogsScreenContent(
        logs = logs,
        logCount = logCount,
        filter = filter,
        selectedLog = selectedLog,
        logScreenState = logScreenState
    )
}
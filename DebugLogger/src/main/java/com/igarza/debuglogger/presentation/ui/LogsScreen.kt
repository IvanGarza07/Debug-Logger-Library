package com.igarza.debuglogger.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.igarza.debuglogger.domain.enums.LogLevel
import com.igarza.debuglogger.domain.model.LogEntry
import com.igarza.debuglogger.domain.model.LogFilter
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogsScreen(
    viewModel: LogsViewModel = hiltViewModel()
) {
    val logs by viewModel.logs.collectAsStateWithLifecycle()
    val logCount by viewModel.logCount.collectAsStateWithLifecycle()
    val filter by viewModel.filter.collectAsStateWithLifecycle()
    val selectedLog by viewModel.selectedLog.collectAsStateWithLifecycle()

    var showFilterSheet by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Debug Logger")
                        Text(
                            "$logCount logs",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterSheet = true }) {
                        Badge(
                            containerColor = if (isFilterActive(filter))
                                MaterialTheme.colorScheme.primary
                            else Color.Transparent
                        ) {
                            Icon(Icons.Default.FilterList, "Filter")
                        }
                    }
                    IconButton(onClick = { showClearDialog = true }) {
                        Icon(Icons.Default.DeleteSweep, "Clear all")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (logs.isEmpty()) {
                EmptyState()
            } else {
                LogsList(
                    logs = logs,
                    selectedLog = selectedLog,
                    onLogClick = { viewModel.selectLog(it) },
                    onLogDelete = { viewModel.deleteLog(it) }
                )
            }
        }
    }

    // Filter Bottom Sheet
    if (showFilterSheet) {
        FilterBottomSheet(
            filter = filter,
            onDismiss = {
                showFilterSheet = false
                Timber.tag("LogsScreen").d("Filter Bottom Sheet: $showFilterSheet")
            },
            onSearchChange = { viewModel.updateSearchQuery(it) },
            onToggleLevel = { viewModel.toggleLogLevel(it) },
            onClearFilter = { viewModel.clearFilter() }
        )
    }

    // Clear Confirmation Dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = {
                showClearDialog = false
                Timber.tag("LogsScreen").d("Clear Bottom Sheet: $showClearDialog")
            },
            title = { Text("Clear All Logs?") },
            text = { Text("This will permanently delete all $logCount logs.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllLogs()
                        showClearDialog = false
                        Timber.tag("LogsScreen").d("Clear Bottom Sheet: $showClearDialog")
                    }
                ) {
                    Text("Clear", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showClearDialog = false
                    Timber.tag("LogsScreen").d("Clear Bottom Sheet: $showClearDialog")
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Log Detail Sheet
    selectedLog?.let { log ->
        LogDetailSheet(
            log = log,
            onDismiss = { viewModel.selectLog(null) },
            onDelete = {
                viewModel.deleteLog(log)
                viewModel.selectLog(null)
            }
        )
    }
}

@Composable
private fun LogsList(
    logs: List<LogEntry>,
    selectedLog: LogEntry?,
    onLogClick: (LogEntry) -> Unit,
    onLogDelete: (LogEntry) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(logs, key = { it.id }) { log ->
            LogItemCard(
                log = log,
                isSelected = log.id == selectedLog?.id,
                onClick = { onLogClick(log) }
            )
        }
    }
}

@Composable
private fun LogItemCard(
    log: LogEntry,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.secondaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Level indicator
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(48.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(getLogLevelColor(log.level))
            )

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = log.tag,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = dateFormat.format(Date(log.timestamp)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = log.message,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (log.throwable != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            "Exception attached",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(getLogLevelColor(log.level).copy(alpha = 0.2f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = log.level.displayName.first().toString(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = getLogLevelColor(log.level)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    filter: LogFilter,
    onDismiss: () -> Unit,
    onSearchChange: (String) -> Unit,
    onToggleLevel: (LogLevel) -> Unit,
    onClearFilter: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Filter Logs",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                TextButton(onClick = onClearFilter) {
                    Text("Clear All")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search field
            OutlinedTextField(
                value = filter.searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = {
                    if (filter.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Default.Clear, "Clear search")
                        }
                    }
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Log Levels",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Log level filters
            LogLevel.entries.forEach { level ->
                LogLevelFilterChip(
                    level = level,
                    isSelected = filter.selectedLevels.contains(level),
                    onClick = { onToggleLevel(level) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun LogLevelFilterChip(
    level: LogLevel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(level.displayName) },
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(getLogLevelColor(level))
            )
        }
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogDetailSheet(
    log: LogEntry,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm:ss.SSS", Locale.getDefault()) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Log Details",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        Icons.Default.Delete,
                        "Delete log",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Level badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(getLogLevelColor(log.level).copy(alpha = 0.2f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        log.level.displayName.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = getLogLevelColor(log.level)
                    )
                }

                Text(
                    log.tag,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                dateFormat.format(Date(log.timestamp)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(16.dp))

            // Message
            Text(
                "Message",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    log.message,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Exception if present
            log.throwable?.let { exception ->
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Exception",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        exception,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                Timber.tag("LogsScreen").d("Delete Bottom Sheet: $showDeleteDialog")
            },
            title = { Text("Delete Log?") },
            text = { Text("This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                        Timber.tag("LogsScreen").d("Delete Bottom Sheet: $showDeleteDialog")
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    Timber.tag("LogsScreen").d("Delete Bottom Sheet: $showDeleteDialog")
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.FormatListNumbered,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Text(
                "No logs yet",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "Logs will appear here as you use the app",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

fun getLogLevelColor(level: LogLevel): Color {
    return when (level) {
        LogLevel.VERBOSE -> Color(0xFF9E9E9E)
        LogLevel.DEBUG -> Color(0xFF2196F3)
        LogLevel.INFO -> Color(0xFF4CAF50)
        LogLevel.WARN -> Color(0xFFFF9800)
        LogLevel.ERROR -> Color(0xFFF44336)
        LogLevel.WTF -> Color(0xFF9C27B0)
    }
}

fun isFilterActive(filter: LogFilter): Boolean {
    return filter.searchQuery.isNotEmpty() ||
            filter.selectedLevels.size != LogLevel.entries.size ||
            filter.startTime != null ||
            filter.endTime != null
}
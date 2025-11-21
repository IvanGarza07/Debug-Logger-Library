package com.igarza.debugloggerexample.presentation.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.igarza.debuglogger.data.logger.DebugLogger
import com.igarza.debuglogger.presentation.ui.LogsActivity
import com.igarza.debugloggerexample.SampleViewModel
import com.igarza.debugloggerexample.presentation.components.InfoCard
import com.igarza.debugloggerexample.presentation.components.LogLevelCard
import com.igarza.debugloggerexample.presentation.components.PresentationCard
import com.igarza.debugloggerexample.presentation.components.QuickActionButton
import com.igarza.debugloggerexample.presentation.components.StressTestCard
import com.igarza.debugloggerexample.presentation.theme.DebugLoggerTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    private val sampleViewModel: SampleViewModel by viewModels {
        SampleViewModel.provideFactory()
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        sampleViewModel.logNotificationPermission(isGranted)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    sampleViewModel.logAlready()
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
        setContent {
            DebugLoggerTheme {
                DebugLoggerDemoScreen(sampleViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugLoggerDemoScreen(
    sampleViewModel: SampleViewModel
) {
    val context = LocalContext.current
    var logCount by remember { mutableIntStateOf(0) }
    var isLogging by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Debug Logger Demo",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = {
                        context.startActivity(Intent(context, LogsActivity::class.java))
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.List,
                            contentDescription = "View Logs",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Presentation Section
                item {
                    PresentationCard(logCount = logCount)
                }

                // Quick Actions
                item {
                    Text(
                        "Quick Actions",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickActionButton(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Visibility,
                            label = "View Logs",
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            context.startActivity(Intent(context, LogsActivity::class.java))
                        }

                        QuickActionButton(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Delete,
                            label = "Clear All",
                            color = MaterialTheme.colorScheme.error
                        ) {
                            sampleViewModel.clearAllLogs()
                            logCount = 0
                        }
                    }
                }

                // Log Level Examples
                item {
                    Text(
                        "Try Different Log Levels",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(sampleViewModel.logLevelExamples) { example ->
                    LogLevelCard(
                        example = example,
                        isLogging = isLogging,
                        onLogClick = {
                            scope.launch {
                                isLogging = true
                                example.action()
                                logCount++
                                delay(300)
                                isLogging = false
                            }
                        }
                    )
                }

                // Stress Test Section
                item {
                    Text(
                        "Stress Testing",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                item {
                    StressTestCard(
                        onTest = { count ->
                            scope.launch {
                                repeat(count) { i ->
                                    when (i % 6) {
                                        0 -> DebugLogger.v("StressTest", "Verbose log #$i")
                                        1 -> DebugLogger.d("StressTest", "Debug log #$i")
                                        2 -> DebugLogger.i("StressTest", "Info log #$i")
                                        3 -> DebugLogger.w("StressTest", "Warning log #$i")
                                        4 -> DebugLogger.e("StressTest", "Error log #$i")
                                        5 -> DebugLogger.a("StressTest", "WTF log #$i")
                                    }
                                    logCount++
                                    delay(10)
                                }
                            }
                        }
                    )
                }

                // Info Card
                item {
                    InfoCard()
                }

                // Bottom spacing
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
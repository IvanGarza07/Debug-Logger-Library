package com.igarza.debuglogger.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.igarza.debuglogger.presentation.theme.DebugLoggerTheme

class LogsActivity : ComponentActivity() {

    private val viewModel: LogsViewModel by viewModels {
        LogsViewModel.provideFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DebugLoggerTheme {
                LogsScreen(viewModel)
            }
        }
    }
}
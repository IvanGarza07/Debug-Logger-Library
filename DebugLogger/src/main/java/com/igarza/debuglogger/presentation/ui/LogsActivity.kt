package com.igarza.debuglogger.presentation.ui

import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.igarza.debuglogger.presentation.theme.DebugLoggerTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class LogsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DebugLoggerTheme {
                LogsScreen()
            }
        }
    }

    override fun onNewIntent(intent: Intent, caller: ComponentCaller) {
        super.onNewIntent(intent, caller)
        Timber.tag("INTENT").d("onNewIntent: ${intent.action}")
        Timber.tag("INTENT").d("onNewIntent: ${intent.extras?.getBoolean("clear_logs")}")
    }
}
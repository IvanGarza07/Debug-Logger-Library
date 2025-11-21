package com.igarza.debugloggerexample

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.igarza.debuglogger.data.logger.DebugLogger
import com.igarza.debuglogger.domain.enums.LogLevel

class SampleViewModel() : ViewModel() {

    val logLevelExamples = listOf(
        LogExample(
            level = LogLevel.VERBOSE,
            icon = Icons.Default.RemoveRedEye,
            color = Color(0xFF9E9E9E),
            description = "Detailed information for debugging",
            action = { DebugLogger.v("Demo", "This is a verbose log message with detailed information") }
        ),
        LogExample(
            level = LogLevel.DEBUG,
            icon = Icons.Default.BugReport,
            color = Color(0xFF2196F3),
            description = "Debug information for developers",
            action = { DebugLogger.d("Demo", "Debug log: User clicked button, processing request...") }
        ),
        LogExample(
            level = LogLevel.INFO,
            icon = Icons.Default.Info,
            color = Color(0xFF4CAF50),
            description = "General informational messages",
            action = { DebugLogger.i("Demo", "User successfully logged in to the application") }
        ),
        LogExample(
            level = LogLevel.WARN,
            icon = Icons.Default.Warning,
            color = Color(0xFFFF9800),
            description = "Warning messages for potential issues",
            action = { DebugLogger.w("Demo", "API response time exceeded 2 seconds - consider caching") }
        ),
        LogExample(
            level = LogLevel.ERROR,
            icon = Icons.Default.Error,
            color = Color(0xFFF44336),
            description = "Error messages for failures",
            action = {
                DebugLogger.e(
                    "Demo",
                    "Network request failed",
                    Exception("Connection timeout after 30 seconds")
                )
            }
        ),
        LogExample(
            level = LogLevel.WTF,
            icon = Icons.Default.ReportProblem,
            color = Color(0xFF9C27B0),
            description = "Critical errors that shouldn't happen",
            action = {
                DebugLogger.a(
                    "Demo",
                    "Critical: Null pointer in required field!",
                    NullPointerException("User object was unexpectedly null")
                )
            }
        )
    )

    fun logNotificationPermission(isGranted: Boolean) {
        if (isGranted) {
            DebugLogger.i("MainActivity", "Notification permission granted")
        } else {
            DebugLogger.w("MainActivity", "Notification permission denied")
        }
    }

    fun logAlready() {
        DebugLogger.i("MainActivity", "Notification permission already granted")
    }

    fun clearAllLogs() {
        DebugLogger.clearAll()
    }

    fun triggerLogs() {
        // Logging básico
        DebugLogger.d("SampleViewModel", "Function called")

        // Logging con diferentes niveles
        DebugLogger.v("TAG", "Verbose message")
        DebugLogger.i("TAG", "Info message")
        DebugLogger.w("TAG", "Warning message")
        DebugLogger.a("TAG", "Assert message")

        // Logging de errores con exception
        try {
            performRiskyOperation()
        } catch (e: Exception) {
            DebugLogger.e("SampleViewModel", "Error occurred", e)
        }
    }

    private fun performRiskyOperation() {
        throw Exception("Something went wrong")
    }

    companion object {
        fun provideFactory(): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(SampleViewModel::class.java)) {
                        return SampleViewModel() as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }

    data class LogExample(
        val level: LogLevel,
        val icon: ImageVector,
        val color: Color,
        val description: String,
        val action: () -> Unit
    )
}
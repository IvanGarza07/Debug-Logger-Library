package com.igarza.debugloggerexample

import androidx.lifecycle.ViewModel
import com.igarza.debuglogger.data.logger.DebugLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SampleViewModel @Inject constructor(
    private val debugLogger: DebugLogger
) : ViewModel() {

    fun logNotificationPermission(isGranted: Boolean) {
        if (isGranted) {
            debugLogger.info("MainActivity", "Notification permission granted")
        } else {
            debugLogger.warn("MainActivity", "Notification permission denied")
        }
    }

    fun logAlready() {
        debugLogger.info("MainActivity", "Notification permission already granted")
    }

    fun triggerLogs() {
        // Logging básico
        debugLogger.debug("SampleViewModel", "Function called")

        // Logging con diferentes niveles
        debugLogger.verbose("TAG", "Verbose message")
        debugLogger.info("TAG", "Info message")
        debugLogger.warn("TAG", "Warning message")

        // Logging de errores con exception
        try {
            performRiskyOperation()
        } catch (e: Exception) {
            debugLogger.error("SampleViewModel", "Error occurred", e)
        }
    }

    private fun performRiskyOperation() {
        throw Exception("Something went wrong")
    }
}
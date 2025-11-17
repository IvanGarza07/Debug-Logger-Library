package com.igarza.debugloggerexample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.igarza.debuglogger.data.logger.DebugLogger

class SampleViewModel() : ViewModel() {

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
}
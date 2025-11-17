package com.igarza.debuglogger.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.igarza.debuglogger.di.LoggerInstance

class ClearDatabaseWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val repository by lazy {
        LoggerInstance.repository
    }

    private val notificationManager by lazy {
        LoggerInstance.notificationManager
    }

    override suspend fun doWork(): Result {
        notificationManager.hideNotification()
        repository.clearAllLogs()
        return Result.success()
    }

    companion object {
        fun enqueue(context: Context) {
            val workRequest = OneTimeWorkRequestBuilder<ClearDatabaseWorker>().build()
            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }
}
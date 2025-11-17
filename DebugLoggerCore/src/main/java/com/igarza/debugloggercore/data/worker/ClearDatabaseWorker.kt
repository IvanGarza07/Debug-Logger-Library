package com.igarza.debugloggercore.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.igarza.debugloggercore.data.notification.LogNotificationManager
import com.igarza.debugloggercore.di.LoggerInstance
import com.igarza.debugloggercore.domain.repository.LogRepository

class ClearDatabaseWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val repository by lazy {
        LoggerInstance.getRepository(applicationContext)
    }

    private val notificationManager by lazy {
        LoggerInstance.getNotificationManager(applicationContext)
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
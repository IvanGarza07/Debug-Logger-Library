package com.igarza.debuglogger.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.igarza.debuglogger.data.notification.LogNotificationManager
import com.igarza.debuglogger.domain.repository.LogRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class ClearDatabaseWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: LogRepository,
    private val notificationManager: LogNotificationManager
) : CoroutineWorker(appContext, workerParams) {

    init {
        Timber.tag("WORKER").d("Worker CREATED")
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
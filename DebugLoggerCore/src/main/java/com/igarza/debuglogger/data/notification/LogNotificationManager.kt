package com.igarza.debuglogger.data.notification

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.igarza.debuglogger.data.notification.NotificationUtils.NOTIFICATION_ID
import com.igarza.debuglogger.domain.model.LogEntry
import com.igarza.debuglogger.presentation.ui.LogsActivity

class LogNotificationManager(private val context: Context) {

    val notificationManager = NotificationManagerCompat.from(context)

    init {
        NotificationUtils.createNotificationChannel(notificationManager)
    }

    fun showNotification(latestLogs: List<LogEntry>) {
        NotificationUtils.showNotification(
            context = context,
            latestLogs = latestLogs,
            activity = LogsActivity::class.java,
            notificationManager = notificationManager
        )
    }

    fun hideNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }

}
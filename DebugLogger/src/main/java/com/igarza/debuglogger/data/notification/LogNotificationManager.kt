package com.igarza.debuglogger.data.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.igarza.debuglogger.R
import com.igarza.debuglogger.domain.model.LogEntry
import com.igarza.debuglogger.presentation.ui.LogsActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogNotificationManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val notificationManager = NotificationManagerCompat.from(context)
    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    companion object {
        private const val CHANNEL_ID = "debug_logger_channel"
        private const val NOTIFICATION_ID = 12345
        private const val INTENT_REQUEST_CODE = 11
        const val ACTION_CLEAR_LOGS = "ACTION_CLEAR_LOGS"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Debug Logger",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows recent application logs"
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("MissingPermission")
    fun showNotification(latestLogs: List<LogEntry>) {
        if (!hasNotificationPermission()) return
        if (latestLogs.isEmpty()) return

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_log_notification)
            .setContentTitle("Debug Logger")
            .setContentText(latestLogs.firstOrNull()?.message ?: "")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setAutoCancel(true)
            .setStyle(buildInboxStyle(latestLogs))
            .setContentIntent(getActivityPendingIntent())
            .addAction(getClearAction())
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun buildInboxStyle(logs: List<LogEntry>): NotificationCompat.InboxStyle {
        val style = NotificationCompat.InboxStyle()
            .setBigContentTitle("Recent Logs (${logs.size})")

        logs.take(5).forEach { log ->
            val time = dateFormat.format(Date(log.timestamp))
            val levelIcon = when (log.level.priority) {
                2 -> "🔍" // VERBOSE
                3 -> "🐛" // DEBUG
                4 -> "ℹ️" // INFO
                5 -> "⚠️" // WARN
                6, 7 -> "❌" // ERROR, WTF
                else -> "📝"
            }
            style.addLine("$time $levelIcon ${log.message.take(50)}")
        }

        if (logs.size > 5) {
            style.setSummaryText("+${logs.size - 5} more logs")
        }

        return style
    }

    private fun getActivityPendingIntent(): PendingIntent {
        val intent = Intent(context, LogsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun getClearAction(): NotificationCompat.Action {
        val clearIntent = Intent(context, LogNotificationReceiver::class.java)
        val clearPendingIntent = PendingIntent.getBroadcast(
            context,
            INTENT_REQUEST_CODE,
            clearIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Action(
            R.drawable.ic_clear,
            "Clear",
            clearPendingIntent
        )
    }

    fun hideNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

}
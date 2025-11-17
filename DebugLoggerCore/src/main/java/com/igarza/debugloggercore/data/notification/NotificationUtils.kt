package com.igarza.debugloggercore.data.notification

import android.Manifest
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
import com.igarza.debugloggercore.R
import com.igarza.debugloggercore.data.dates.DateUtils
import com.igarza.debugloggercore.data.dates.DateUtils.LOG_FORMAT
import com.igarza.debugloggercore.domain.model.LogEntry

object NotificationUtils {

    const val NOTIFICATION_ID = 12345
    const val CHANNEL_ID = "debug_logger_channel"
    private const val INTENT_REQUEST_CODE = 11

    fun createNotificationChannel(notificationManager: NotificationManagerCompat) {
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

    fun showNotification(
        context: Context,
        latestLogs: List<LogEntry>,
        activity: Class<*>,
        notificationManager: NotificationManagerCompat
    ) {
        if (!hasNotificationPermission(context)) return
        if (latestLogs.isEmpty()) return

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_log_notification)
            .setContentTitle("Debug Logger")
            .setContentText(latestLogs.firstOrNull()?.message ?: "")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setAutoCancel(true)
            .setStyle(buildInboxStyle(latestLogs))
            .setContentIntent(getActivityPendingIntent(context, activity))
            .addAction(getClearAction(context))
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun buildInboxStyle(logs: List<LogEntry>): NotificationCompat.InboxStyle {
        val style = NotificationCompat.InboxStyle()
            .setBigContentTitle("Recent Logs (${logs.size})")

        logs.take(5).forEach { log ->
            val time = DateUtils.formatTimestamp(log.timestamp, LOG_FORMAT)
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

    private fun getActivityPendingIntent(context: Context, activity: Class<*>): PendingIntent {
        val intent = Intent(context, activity).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun getClearAction(context: Context): NotificationCompat.Action {
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
}
package com.igarza.debuglogger.data.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.igarza.debuglogger.presentation.ui.LogsActivity
import timber.log.Timber

internal class LogNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Timber.tag("INTENT").d("Receiver class: ${this::class.java.name}")
        Timber.tag("INTENT").d("onNewIntent: ${intent.action}")
        Toast.makeText(context, "onNewIntent: ${intent.action}", Toast.LENGTH_SHORT).show()
        if (intent.action == LogNotificationManager.ACTION_CLEAR_LOGS) {
            // This will be handled by the ViewModel through the repository
            val clearIntent = Intent(context, LogsActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra("clear_logs", true)
            }
            context.startActivity(clearIntent)
        }
    }
}
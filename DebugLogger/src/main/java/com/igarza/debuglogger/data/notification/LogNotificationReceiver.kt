package com.igarza.debuglogger.data.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.igarza.debuglogger.data.worker.ClearDatabaseWorker

class LogNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(context, "Logs cleared", Toast.LENGTH_SHORT).show()
        ClearDatabaseWorker.enqueue(context)
    }
}
package com.igarza.debuglogger.data.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.igarza.debuglogger.data.worker.ClearDatabaseWorker

class LogNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        ClearDatabaseWorker.enqueue(context)
    }
}
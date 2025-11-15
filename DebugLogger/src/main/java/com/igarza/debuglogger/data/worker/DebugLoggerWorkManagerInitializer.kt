package com.igarza.debuglogger.data.worker

import android.content.Context
import androidx.startup.Initializer
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.hilt.android.EntryPointAccessors

class DebugLoggerWorkManagerInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context,
            WorkerFactoryEntryPoint::class.java
        )

        WorkManager.initialize(
            context,
            Configuration.Builder()
                .setWorkerFactory(entryPoint.getWorkerFactory())
                .build()
        )
    }

    override fun dependencies() = emptyList<Class<out Initializer<*>>>()
}
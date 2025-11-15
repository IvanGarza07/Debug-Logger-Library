package com.igarza.debuglogger.data.worker

import androidx.hilt.work.HiltWorkerFactory
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WorkerFactoryEntryPoint {
    fun getWorkerFactory(): HiltWorkerFactory
}
package com.igarza.debuglogger.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.igarza.debuglogger.data.database.LogDao
import com.igarza.debuglogger.domain.enums.LogLevel
import com.igarza.debuglogger.domain.model.LogEntry
import com.igarza.debuglogger.domain.model.LogFilter
import com.igarza.debuglogger.utils.CoroutineTestRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner.StrictStubs::class)
class LogRepositoryImplTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = CoroutineTestRule()

    @Mock
    private lateinit var logDao: LogDao

    private lateinit var logRepository: LogRepositoryImpl

    @Before
    fun setUp() {
        logRepository = LogRepositoryImpl(logDao)
    }

    @Test
    fun `assert getAllLogs should return logs from dao`() = runTest {
        val sampleLogs = listOf(
            LogEntry(id = 1, message = "Test 1", level = LogLevel.DEBUG, tag = "TestTag"),
            LogEntry(id = 2, message = "Test 2", level = LogLevel.ERROR, tag = "TestTag")
        )

        whenever(logDao.getAllLogs()).thenReturn(flowOf(sampleLogs))

        val result = logRepository.getAllLogs()

        assertEquals(sampleLogs, result.first())
    }

    @Test
    fun `assert getLatestLogs delegates to dao`() = runTest {
        val sampleLogs = listOf(
            LogEntry(id = 1, message = "Test 1", level = LogLevel.DEBUG, tag = "TestTag")
        )

        whenever(logDao.getLatestLogs(10)).thenReturn(flowOf(sampleLogs))

        val result = logRepository.getLatestLogs(10).first()

        assertEquals(sampleLogs, result)
    }

    @Test
    fun `assert getFilteredLogs calls dao with correct params`() = runTest {
        val filter = LogFilter(
            searchQuery = "error",
            selectedLevels = listOf(LogLevel.ERROR).toSet(),
            startTime = 1000L,
            endTime = 2000L
        )

        val filteredLogs = listOf(
            LogEntry(id = 1, message = "Test 1 error", level = LogLevel.ERROR, tag = "TestTag")
        )

        whenever(
            logDao.getFilteredLogs(
                query = "error",
                levels = listOf(LogLevel.ERROR.priority),
                startTime = 1000,
                endTime = 2000
            )
        ).thenReturn(flowOf(filteredLogs))

        val result = logRepository.getFilteredLogs(filter).first()

        assertEquals(filteredLogs, result)
    }

    @Test
    fun `assert insertLog delegates to dao`() = runTest {
        val entry = LogEntry(id = 1, message = "Test 1 error", level = LogLevel.DEBUG, tag = "TestTag")
        whenever(logDao.insertLog(entry)).thenReturn(10L)

        val result = logRepository.insertLog(entry)

        assertEquals(10L, result)
        verify(logDao).insertLog(entry)
    }

    @Test
    fun `assert clearAllLogs delegates to dao`() = runTest {
        logRepository.clearAllLogs()
        verify(logDao).clearAll()
    }

    @Test
    fun `assert deleteLog delegates to dao`() = runTest {
        logRepository.deleteLog(5L)
        verify(logDao).deleteLog(5L)
    }

    @Test
    fun `assert maintainMaxLogs delegates to dao`() = runTest {
        logRepository.maintainMaxLogs(100)
        verify(logDao).deleteOldLogs(100)
    }

    @Test
    fun `assert getLogCount returns flow from dao`() = runTest {
        whenever(logDao.getLogCount()).thenReturn(flowOf(42))

        val result = logRepository.getLogCount().first()

        assertEquals(42, result)
    }

}
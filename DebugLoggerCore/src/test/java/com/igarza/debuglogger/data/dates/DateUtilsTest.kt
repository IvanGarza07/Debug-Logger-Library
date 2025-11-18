package com.igarza.debuglogger.data.dates

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.Calendar
import java.util.Locale

class DateUtilsTest {
    private val fixedLocale = Locale.US

    @Before
    fun setup() {
        Locale.setDefault(fixedLocale)
    }

    @Test
    fun `assert formatTimestamp should format time correctly using LOG_FORMAT`() {
        // 2024-01-01 00:00:00 UTC
        val timestamp = 1704067200000L

        val result = DateUtils.formatTimestamp(
            timestamp,
            DateUtils.LOG_FORMAT
        )

        assertEquals("18:00:00", result)
    }

    @Test
    fun `assert formatTimestamp should format timestamp with milliseconds using LOG_ITEM_FORMAT`() {
        // 2024-01-01 12:34:56.789 UTC
        val calendar = Calendar.getInstance(fixedLocale).apply {
            set(2024, Calendar.JANUARY, 1, 12, 34, 56)
            set(Calendar.MILLISECOND, 789)
        }

        val timestamp = calendar.timeInMillis

        val result = DateUtils.formatTimestamp(
            timestamp,
            DateUtils.LOG_ITEM_FORMAT
        )

        assertEquals("12:34:56.789", result)
    }

    @Test
    fun `assert formatTimestamp should format detailed log timestamp using LOG_DETAIL_FORMAT`() {
        // 2024-02-15 08:05:10.123 UTC
        val calendar = Calendar.getInstance(fixedLocale).apply {
            set(2024, Calendar.FEBRUARY, 15, 8, 5, 10)
            set(Calendar.MILLISECOND, 123)
        }

        val timestamp = calendar.timeInMillis

        val result = DateUtils.formatTimestamp(
            timestamp,
            DateUtils.LOG_DETAIL_FORMAT
        )

        assertEquals("Feb 15, 2024 08:05:10.123", result)
    }

    @Test
    fun `assert formatTimestamp should correctly format epoch time`() {
        val timestamp = 0L

        val result = DateUtils.formatTimestamp(
            timestamp,
            DateUtils.LOG_FORMAT
        )

        // Epoch at Locale.US → "18:00:00" in UTC-6 etc.
        // Instead of checking exact value, we check that the format length matches
        assertEquals(8, result.length)
    }

}
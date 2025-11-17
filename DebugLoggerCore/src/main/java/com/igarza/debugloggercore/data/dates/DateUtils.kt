package com.igarza.debugloggercore.data.dates

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {

    const val LOG_FORMAT = "HH:mm:ss"

    fun formatTimestamp(timestamp: Long, format: String): String {
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }
}
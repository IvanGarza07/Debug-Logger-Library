package com.igarza.debuglogger.data.dates

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {

    const val LOG_FORMAT = "HH:mm:ss"
    const val LOG_ITEM_FORMAT = "HH:mm:ss.SSS"
    const val LOG_DETAIL_FORMAT = "MMM dd, yyyy HH:mm:ss.SSS"

    fun formatTimestamp(timestamp: Long, format: String): String {
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }
}
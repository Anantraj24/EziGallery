package com.ezi.gallery.core.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateFormatter {
    private val fullDateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
    private val detailedDateTimeFormat = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault())

    fun formatGroupHeader(timestampMillis: Long): String {
        if (timestampMillis <= 0L) return "Unknown Date"

        val targetCal = Calendar.getInstance().apply {
            timeInMillis = timestampMillis
        }
        val nowCal = Calendar.getInstance()

        val isSameYear = targetCal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR)
        val targetDayOfYear = targetCal.get(Calendar.DAY_OF_YEAR)
        val nowDayOfYear = nowCal.get(Calendar.DAY_OF_YEAR)

        return when {
            isSameYear && targetDayOfYear == nowDayOfYear -> "Today"
            isSameYear && targetDayOfYear == nowDayOfYear - 1 -> "Yesterday"
            else -> fullDateFormat.format(Date(timestampMillis))
        }
    }

    fun formatDetailedDateTime(timestampMillis: Long): String {
        if (timestampMillis <= 0L) return "Unknown date"
        return detailedDateTimeFormat.format(Date(timestampMillis))
    }
}

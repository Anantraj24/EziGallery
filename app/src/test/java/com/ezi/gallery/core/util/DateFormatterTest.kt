package com.ezi.gallery.core.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.util.Calendar

class DateFormatterTest {

    @Test
    fun testFormatGroupHeader_invalidOrZero() {
        assertEquals("Unknown Date", DateFormatter.formatGroupHeader(0L))
        assertEquals("Unknown Date", DateFormatter.formatGroupHeader(-12345L))
    }

    @Test
    fun testFormatGroupHeader_today() {
        val now = System.currentTimeMillis()
        assertEquals("Today", DateFormatter.formatGroupHeader(now))
    }

    @Test
    fun testFormatGroupHeader_yesterday() {
        val cal = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }
        assertEquals("Yesterday", DateFormatter.formatGroupHeader(cal.timeInMillis))
    }

    @Test
    fun testFormatDetailedDateTime_zeroAndValid() {
        assertEquals("Unknown date", DateFormatter.formatDetailedDateTime(0L))
        val formatted = DateFormatter.formatDetailedDateTime(System.currentTimeMillis())
        assertNotNull(formatted)
    }
}

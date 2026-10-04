package com.ezi.gallery.core.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FileUtilsTest {

    @Test
    fun testFormatFileSize_zeroAndNegative() {
        assertEquals("0 B", FileUtils.formatFileSize(0L))
        assertEquals("0 B", FileUtils.formatFileSize(-100L))
    }

    @Test
    fun testFormatFileSize_bytes() {
        val result = FileUtils.formatFileSize(500L)
        assertTrue(result.contains("B"))
    }

    @Test
    fun testFormatFileSize_kilobytes() {
        val result = FileUtils.formatFileSize(2048L)
        assertTrue(result.contains("KB"))
    }

    @Test
    fun testFormatFileSize_megabytes() {
        val result = FileUtils.formatFileSize(10 * 1024 * 1024L)
        assertTrue(result.contains("MB"))
    }

    @Test
    fun testFormatFileSize_gigabytes() {
        val result = FileUtils.formatFileSize(4L * 1024 * 1024 * 1024)
        assertTrue(result.contains("GB"))
    }

    @Test
    fun testFormatDuration_zeroAndNegative() {
        assertEquals("0:00", FileUtils.formatDuration(0L))
        assertEquals("0:00", FileUtils.formatDuration(-5000L))
    }

    @Test
    fun testFormatDuration_secondsOnly() {
        assertEquals("0:45", FileUtils.formatDuration(45_000L))
    }

    @Test
    fun testFormatDuration_minutesAndSeconds() {
        assertEquals("3:15", FileUtils.formatDuration(195_000L))
    }

    @Test
    fun testFormatDuration_hoursMinutesAndSeconds() {
        assertEquals("1:05:30", FileUtils.formatDuration(3930_000L))
    }
}

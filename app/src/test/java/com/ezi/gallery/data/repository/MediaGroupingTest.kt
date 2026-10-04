package com.ezi.gallery.data.repository

import android.net.Uri
import com.ezi.gallery.core.model.MediaItem
import com.ezi.gallery.core.util.DateFormatter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class MediaGroupingTest {

    private fun createMediaItem(id: Long, dateTaken: Long): MediaItem {
        val mockUri = org.mockito.Mockito.mock(Uri::class.java)
        return MediaItem(
            id = id,
            contentUri = mockUri,
            displayName = "item_$id.jpg",
            mimeType = "image/jpeg",
            isVideo = false,
            dateTaken = dateTaken,
            size = 1024L,
            width = 1920,
            height = 1080
        )
    }

    private fun groupMediaByDate(items: List<MediaItem>) =
        items.groupBy { DateFormatter.formatGroupHeader(it.dateTaken) }

    @Test
    fun testEmptyMediaListGrouping() {
        val groups = groupMediaByDate(emptyList())
        assertTrue(groups.isEmpty())
    }

    @Test
    fun testSameDayGrouping() {
        val now = System.currentTimeMillis()
        val item1 = createMediaItem(1L, now)
        val item2 = createMediaItem(2L, now - 1000)

        val groups = groupMediaByDate(listOf(item1, item2))
        assertEquals(1, groups.size)
        assertEquals("Today", groups.keys.first())
        assertEquals(2, groups["Today"]?.size)
    }

    @Test
    fun testMultipleDaysGrouping() {
        val now = System.currentTimeMillis()
        val yesterdayCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

        val item1 = createMediaItem(1L, now)
        val item2 = createMediaItem(2L, yesterdayCal.timeInMillis)

        val groups = groupMediaByDate(listOf(item1, item2))
        assertEquals(2, groups.size)
        assertTrue(groups.containsKey("Today"))
        assertTrue(groups.containsKey("Yesterday"))
    }
}

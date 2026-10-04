package com.ezi.gallery.core.model

import android.net.Uri

data class MediaItem(
    val id: Long,
    val contentUri: Uri,
    val displayName: String,
    val mimeType: String,
    val isVideo: Boolean,
    val dateTaken: Long, // Epoch millis
    val size: Long, // in bytes
    val width: Int,
    val height: Int,
    val duration: Long = 0L, // in milliseconds
    val bucketId: String = "",
    val bucketName: String = "",
    val isFavorite: Boolean = false
)

data class MediaDateGroup(
    val header: String,
    val items: List<MediaItem>
)

data class AlbumItem(
    val bucketId: String,
    val bucketName: String,
    val coverUri: Uri?,
    val itemCount: Int,
    val isVideoOnly: Boolean = false
)

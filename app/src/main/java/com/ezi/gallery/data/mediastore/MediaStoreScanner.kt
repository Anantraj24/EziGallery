package com.ezi.gallery.data.mediastore

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import com.ezi.gallery.core.model.AlbumItem
import com.ezi.gallery.core.model.MediaItem
import com.ezi.gallery.core.model.SortOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaStoreScanner(private val context: Context) {

    private val contentResolver: ContentResolver get() = context.contentResolver

    suspend fun queryAllMedia(sortOrder: SortOrder = SortOrder.DATE_DESC): List<MediaItem> = withContext(Dispatchers.IO) {
        val mediaList = mutableListOf<MediaItem>()

        // 1. Query Images
        queryTable(
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            isVideo = false,
            sortOrder = sortOrder,
            destination = mediaList
        )

        // 2. Query Videos
        queryTable(
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            isVideo = true,
            sortOrder = sortOrder,
            destination = mediaList
        )

        // Sort combined list in memory based on requested sort order
        sortMediaList(mediaList, sortOrder)
    }

    suspend fun queryAlbumMedia(bucketId: String, sortOrder: SortOrder = SortOrder.DATE_DESC): List<MediaItem> = withContext(Dispatchers.IO) {
        val mediaList = mutableListOf<MediaItem>()

        val selection = "${MediaStore.MediaColumns.BUCKET_ID} = ?"
        val selectionArgs = arrayOf(bucketId)

        queryTable(
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            isVideo = false,
            sortOrder = sortOrder,
            selection = selection,
            selectionArgs = selectionArgs,
            destination = mediaList
        )

        queryTable(
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            isVideo = true,
            sortOrder = sortOrder,
            selection = selection,
            selectionArgs = selectionArgs,
            destination = mediaList
        )

        sortMediaList(mediaList, sortOrder)
    }

    suspend fun queryAlbums(): List<AlbumItem> = withContext(Dispatchers.IO) {
        val allMedia = queryAllMedia(SortOrder.DATE_DESC)
        val albumsMap = linkedMapOf<String, MutableList<MediaItem>>()

        for (item in allMedia) {
            val key = if (item.bucketId.isNotBlank()) item.bucketId else "unknown"
            albumsMap.getOrPut(key) { mutableListOf() }.add(item)
        }

        albumsMap.map { (bucketId, items) ->
            val firstItem = items.firstOrNull()
            val bucketName = firstItem?.bucketName?.ifBlank { "Photos" } ?: "Photos"
            val isAllVideo = items.all { it.isVideo }
            AlbumItem(
                bucketId = bucketId,
                bucketName = bucketName,
                coverUri = firstItem?.contentUri,
                itemCount = items.size,
                isVideoOnly = isAllVideo
            )
        }.sortedByDescending { it.itemCount }
    }

    private fun queryTable(
        uri: Uri,
        isVideo: Boolean,
        sortOrder: SortOrder,
        selection: String? = null,
        selectionArgs: Array<String>? = null,
        destination: MutableList<MediaItem>
    ) {
        val projection = mutableListOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.DATE_TAKEN,
            MediaStore.MediaColumns.DATE_ADDED,
            MediaStore.MediaColumns.DATE_MODIFIED,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT,
            MediaStore.MediaColumns.BUCKET_ID,
            MediaStore.MediaColumns.BUCKET_DISPLAY_NAME
        )

        if (isVideo) {
            projection.add(MediaStore.Video.VideoColumns.DURATION)
        }

        val sqlSort = when (sortOrder) {
            SortOrder.DATE_DESC -> "${MediaStore.MediaColumns.DATE_MODIFIED} DESC"
            SortOrder.DATE_ASC -> "${MediaStore.MediaColumns.DATE_MODIFIED} ASC"
            SortOrder.NAME_ASC -> "${MediaStore.MediaColumns.DISPLAY_NAME} COLLATE NOCASE ASC"
            SortOrder.NAME_DESC -> "${MediaStore.MediaColumns.DISPLAY_NAME} COLLATE NOCASE DESC"
        }

        try {
            val cursor: Cursor? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val queryArgs = Bundle().apply {
                    if (selection != null) {
                        putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection)
                        putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selectionArgs)
                    }
                    putString(ContentResolver.QUERY_ARG_SQL_SORT_ORDER, sqlSort)
                }
                contentResolver.query(uri, projection.toTypedArray(), queryArgs, null)
            } else {
                contentResolver.query(uri, projection.toTypedArray(), selection, selectionArgs, sqlSort)
            }

            cursor?.use { c ->
                val idCol = c.getColumnIndex(MediaStore.MediaColumns._ID)
                val nameCol = c.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                val mimeCol = c.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE)
                val dateTakenCol = c.getColumnIndex(MediaStore.MediaColumns.DATE_TAKEN)
                val dateAddedCol = c.getColumnIndex(MediaStore.MediaColumns.DATE_ADDED)
                val dateModifiedCol = c.getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED)
                val sizeCol = c.getColumnIndex(MediaStore.MediaColumns.SIZE)
                val widthCol = c.getColumnIndex(MediaStore.MediaColumns.WIDTH)
                val heightCol = c.getColumnIndex(MediaStore.MediaColumns.HEIGHT)
                val bucketIdCol = c.getColumnIndex(MediaStore.MediaColumns.BUCKET_ID)
                val bucketNameCol = c.getColumnIndex(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME)
                val durationCol = if (isVideo) c.getColumnIndex(MediaStore.Video.VideoColumns.DURATION) else -1

                while (c.moveToNext()) {
                    val id = if (idCol != -1) c.getLong(idCol) else continue
                    val displayName = if (nameCol != -1) c.getString(nameCol) ?: "Media_$id" else "Media_$id"
                    val mimeType = if (mimeCol != -1) c.getString(mimeCol) ?: if (isVideo) "video/*" else "image/*" else if (isVideo) "video/*" else "image/*"

                    val dateTaken = if (dateTakenCol != -1 && !c.isNull(dateTakenCol)) {
                        c.getLong(dateTakenCol)
                    } else if (dateModifiedCol != -1 && !c.isNull(dateModifiedCol)) {
                        c.getLong(dateModifiedCol) * 1000L
                    } else if (dateAddedCol != -1 && !c.isNull(dateAddedCol)) {
                        c.getLong(dateAddedCol) * 1000L
                    } else {
                        System.currentTimeMillis()
                    }

                    val size = if (sizeCol != -1 && !c.isNull(sizeCol)) c.getLong(sizeCol) else 0L
                    val width = if (widthCol != -1 && !c.isNull(widthCol)) c.getInt(widthCol) else 0
                    val height = if (heightCol != -1 && !c.isNull(heightCol)) c.getInt(heightCol) else 0
                    val duration = if (isVideo && durationCol != -1 && !c.isNull(durationCol)) c.getLong(durationCol) else 0L

                    val bucketId = if (bucketIdCol != -1) c.getString(bucketIdCol) ?: "" else ""
                    val bucketName = if (bucketNameCol != -1) c.getString(bucketNameCol) ?: "" else ""

                    val contentUri = ContentUris.withAppendedId(uri, id)

                    destination.add(
                        MediaItem(
                            id = id,
                            contentUri = contentUri,
                            displayName = displayName,
                            mimeType = mimeType,
                            isVideo = isVideo,
                            dateTaken = dateTaken,
                            size = size,
                            width = width,
                            height = height,
                            duration = duration,
                            bucketId = bucketId,
                            bucketName = bucketName
                        )
                    )
                }
            }
        } catch (e: Exception) {
            // MediaStore queries should fail gracefully on restricted or corrupt tables
            e.printStackTrace()
        }
    }

    private fun sortMediaList(list: MutableList<MediaItem>, sortOrder: SortOrder): List<MediaItem> {
        when (sortOrder) {
            SortOrder.DATE_DESC -> list.sortByDescending { it.dateTaken }
            SortOrder.DATE_ASC -> list.sortBy { it.dateTaken }
            SortOrder.NAME_ASC -> list.sortBy { it.displayName.lowercase() }
            SortOrder.NAME_DESC -> list.sortByDescending { it.displayName.lowercase() }
        }
        return list
    }
}

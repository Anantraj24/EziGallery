package com.ezi.gallery.data.repository

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.IntentSenderRequest
import com.ezi.gallery.core.model.AlbumItem
import com.ezi.gallery.core.model.MediaDateGroup
import com.ezi.gallery.core.model.MediaItem
import com.ezi.gallery.core.model.SortOrder
import com.ezi.gallery.core.util.DateFormatter
import com.ezi.gallery.data.local.AppDatabase
import com.ezi.gallery.data.local.FavoriteEntity
import com.ezi.gallery.data.mediastore.MediaStoreScanner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class MediaRepository(
    private val context: Context,
    private val scanner: MediaStoreScanner,
    private val database: AppDatabase
) {
    private val favoriteDao = database.favoriteDao()

    val favoriteIdsFlow: Flow<Set<Long>> = favoriteDao.getAllFavoriteIdsFlow()
        .map { it.toSet() }
        .flowOn(Dispatchers.IO)

    suspend fun getMedia(sortOrder: SortOrder = SortOrder.DATE_DESC): List<MediaItem> = withContext(Dispatchers.IO) {
        val items = scanner.queryAllMedia(sortOrder)
        val favorites = favoriteDao.getAllFavoriteIdsFlow()
        // Quick pass to mark favorite items
        items
    }

    suspend fun getMediaWithFavorites(sortOrder: SortOrder = SortOrder.DATE_DESC, favoriteIds: Set<Long>): List<MediaItem> = withContext(Dispatchers.Default) {
        val items = scanner.queryAllMedia(sortOrder)
        items.map { item ->
            if (favoriteIds.contains(item.id)) {
                item.copy(isFavorite = true)
            } else {
                item
            }
        }
    }

    suspend fun getAlbumMediaWithFavorites(bucketId: String, sortOrder: SortOrder = SortOrder.DATE_DESC, favoriteIds: Set<Long>): List<MediaItem> = withContext(Dispatchers.Default) {
        val items = scanner.queryAlbumMedia(bucketId, sortOrder)
        items.map { item ->
            if (favoriteIds.contains(item.id)) {
                item.copy(isFavorite = true)
            } else {
                item
            }
        }
    }

    suspend fun getAlbums(): List<AlbumItem> = scanner.queryAlbums()

    suspend fun toggleFavorite(mediaItem: MediaItem) = withContext(Dispatchers.IO) {
        if (favoriteDao.isFavorite(mediaItem.id)) {
            favoriteDao.deleteFavorite(mediaItem.id)
        } else {
            favoriteDao.insertFavorite(
                FavoriteEntity(
                    mediaId = mediaItem.id,
                    contentUriString = mediaItem.contentUri.toString()
                )
            )
        }
    }

    suspend fun setFavorites(mediaItems: List<MediaItem>, favorite: Boolean) = withContext(Dispatchers.IO) {
        if (favorite) {
            mediaItems.forEach { item ->
                favoriteDao.insertFavorite(
                    FavoriteEntity(
                        mediaId = item.id,
                        contentUriString = item.contentUri.toString()
                    )
                )
            }
        } else {
            favoriteDao.deleteFavorites(mediaItems.map { it.id })
        }
    }

    fun groupMediaByDate(items: List<MediaItem>): List<MediaDateGroup> {
        val groups = linkedMapOf<String, MutableList<MediaItem>>()
        for (item in items) {
            val header = DateFormatter.formatGroupHeader(item.dateTaken)
            groups.getOrPut(header) { mutableListOf() }.add(item)
        }
        return groups.map { (header, list) -> MediaDateGroup(header = header, items = list) }
    }

    fun createShareIntent(items: List<MediaItem>): Intent {
        return if (items.size == 1) {
            val item = items.first()
            Intent(Intent.ACTION_SEND).apply {
                type = item.mimeType
                putExtra(Intent.EXTRA_STREAM, item.contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        } else {
            val uris = ArrayList(items.map { it.contentUri })
            val mimeType = if (items.all { !it.isVideo }) "image/*" else if (items.all { it.isVideo }) "video/*" else "*/*"
            Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                type = mimeType
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }
    }

    fun deleteMediaItems(
        activity: Activity,
        items: List<MediaItem>,
        onPendingIntentResult: ((IntentSenderRequest) -> Unit)? = null
    ): Boolean {
        if (items.isEmpty()) return true

        val uris = items.map { it.contentUri }

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val pendingIntent: PendingIntent = MediaStore.createDeleteRequest(activity.contentResolver, uris)
                val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent.intentSender).build()
                onPendingIntentResult?.invoke(intentSenderRequest)
                true
            } else {
                for (item in items) {
                    activity.contentResolver.delete(item.contentUri, null, null)
                }
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

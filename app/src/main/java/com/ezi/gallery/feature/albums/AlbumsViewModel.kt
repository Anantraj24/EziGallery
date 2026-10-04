package com.ezi.gallery.feature.albums

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ezi.gallery.EziGalleryApp
import com.ezi.gallery.core.model.AlbumItem
import com.ezi.gallery.core.model.MediaItem
import com.ezi.gallery.core.model.SortOrder
import com.ezi.gallery.core.permissions.PermissionHelper
import com.ezi.gallery.data.repository.MediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AlbumsUiState(
    val isLoading: Boolean = true,
    val hasPermission: Boolean = false,
    val albums: List<AlbumItem> = emptyList(),
    val albumMedia: List<MediaItem> = emptyList(),
    val selectedAlbumName: String = ""
)

class AlbumsViewModel(
    private val repository: MediaRepository,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlbumsUiState())
    val uiState: StateFlow<AlbumsUiState> = _uiState.asStateFlow()

    init {
        loadAlbums()
    }

    fun loadAlbums() {
        val hasPerm = PermissionHelper.hasMediaPermission(context)
        _uiState.update { it.copy(hasPermission = hasPerm) }
        if (!hasPerm) {
            _uiState.update { it.copy(isLoading = false) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val albums = repository.getAlbums()
            _uiState.update { it.copy(isLoading = false, albums = albums) }
        }
    }

    fun loadAlbumMedia(bucketId: String, bucketName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, selectedAlbumName = bucketName) }
            val favorites = repository.favoriteIdsFlow.first()
            val media = repository.getAlbumMediaWithFavorites(bucketId, SortOrder.DATE_DESC, favorites)
            _uiState.update { it.copy(isLoading = false, albumMedia = media) }
        }
    }

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val app = context.applicationContext as EziGalleryApp
                    return AlbumsViewModel(app.repository, context) as T
                }
            }
    }
}

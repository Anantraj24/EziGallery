package com.ezi.gallery.feature.favorites

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ezi.gallery.EziGalleryApp
import com.ezi.gallery.core.model.MediaItem
import com.ezi.gallery.core.model.SortOrder
import com.ezi.gallery.data.repository.MediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FavoritesUiState(
    val isLoading: Boolean = true,
    val favoriteItems: List<MediaItem> = emptyList()
)

class FavoritesViewModel(
    private val repository: MediaRepository,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            repository.favoriteIdsFlow.collectLatest { favoriteIds ->
                if (favoriteIds.isEmpty()) {
                    _uiState.update { it.copy(isLoading = false, favoriteItems = emptyList()) }
                } else {
                    val allMedia = repository.getMediaWithFavorites(SortOrder.DATE_DESC, favoriteIds)
                    val favoritesOnly = allMedia.filter { it.isFavorite }
                    _uiState.update { it.copy(isLoading = false, favoriteItems = favoritesOnly) }
                }
            }
        }
    }

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val app = context.applicationContext as EziGalleryApp
                    return FavoritesViewModel(app.repository, context) as T
                }
            }
    }
}

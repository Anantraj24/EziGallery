package com.ezi.gallery.feature.search

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ezi.gallery.EziGalleryApp
import com.ezi.gallery.core.model.MediaItem
import com.ezi.gallery.core.model.SortOrder
import com.ezi.gallery.data.repository.MediaRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val isSearching: Boolean = false,
    val results: List<MediaItem> = emptyList(),
    val hasSearched: Boolean = false
)

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val repository: MediaRepository,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val queryFlow = MutableStateFlow("")

    init {
        queryFlow
            .debounce(200)
            .onEach { q ->
                performSearch(q)
            }
            .launchIn(viewModelScope)
    }

    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
        queryFlow.value = newQuery
    }

    private fun performSearch(query: String) {
        if (query.isBlank()) {
            _uiState.update { it.copy(isSearching = false, results = emptyList(), hasSearched = false) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true) }
            val favorites = repository.favoriteIdsFlow.first()
            val allMedia = repository.getMediaWithFavorites(SortOrder.DATE_DESC, favorites)
            val filtered = allMedia.filter { item ->
                item.displayName.contains(query, ignoreCase = true) ||
                        item.bucketName.contains(query, ignoreCase = true)
            }
            _uiState.update {
                it.copy(
                    isSearching = false,
                    results = filtered,
                    hasSearched = true
                )
            }
        }
    }

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val app = context.applicationContext as EziGalleryApp
                    return SearchViewModel(app.repository, app) as T
                }
            }
    }
}

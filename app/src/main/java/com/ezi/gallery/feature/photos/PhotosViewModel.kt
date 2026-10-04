package com.ezi.gallery.feature.photos

import android.app.Activity
import android.content.Context
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ezi.gallery.EziGalleryApp
import com.ezi.gallery.core.model.GridDensity
import com.ezi.gallery.core.model.MediaDateGroup
import com.ezi.gallery.core.model.MediaItem
import com.ezi.gallery.core.model.SortOrder
import com.ezi.gallery.core.permissions.PermissionHelper
import com.ezi.gallery.core.preferences.AppPreferences
import com.ezi.gallery.data.repository.MediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PhotosUiState(
    val isLoading: Boolean = true,
    val hasPermission: Boolean = false,
    val mediaList: List<MediaItem> = emptyList(),
    val dateGroups: List<MediaDateGroup> = emptyList(),
    val selectedIds: Set<Long> = emptySet(),
    val isSelectionMode: Boolean = false,
    val sortOrder: SortOrder = SortOrder.DATE_DESC,
    val gridDensity: GridDensity = GridDensity.THREE,
    val groupByDate: Boolean = true
)

class PhotosViewModel(
    private val repository: MediaRepository,
    private val preferences: AppPreferences,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(PhotosUiState())
    val uiState: StateFlow<PhotosUiState> = _uiState.asStateFlow()

    init {
        checkPermissionAndStart()
    }

    fun checkPermissionAndStart() {
        val hasPerm = PermissionHelper.hasMediaPermission(context)
        _uiState.update { it.copy(hasPermission = hasPerm) }
        if (hasPerm) {
            observePreferencesAndData()
        } else {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun observePreferencesAndData() {
        viewModelScope.launch {
            combine(
                preferences.sortOrderFlow,
                preferences.gridDensityFlow,
                preferences.groupByDateFlow,
                repository.favoriteIdsFlow
            ) { sortOrder, gridDensity, groupByDate, favoriteIds ->
                PreferencesSnapshot(sortOrder, gridDensity, groupByDate, favoriteIds)
            }.collectLatest { snapshot ->
                _uiState.update {
                    it.copy(
                        sortOrder = snapshot.sortOrder,
                        gridDensity = snapshot.gridDensity,
                        groupByDate = snapshot.groupByDate
                    )
                }
                refreshMedia(snapshot.sortOrder, snapshot.groupByDate, snapshot.favoriteIds)
            }
        }
    }

    fun refresh() {
        if (_uiState.value.hasPermission) {
            viewModelScope.launch {
                refreshMedia(_uiState.value.sortOrder, _uiState.value.groupByDate)
            }
        }
    }

    private suspend fun refreshMedia(
        sortOrder: SortOrder,
        groupByDate: Boolean,
        favoriteIdsOverride: Set<Long>? = null
    ) {
        val favoriteIds = favoriteIdsOverride ?: emptySet()
        val items = repository.getMediaWithFavorites(sortOrder, favoriteIds)
        val groups = if (groupByDate) repository.groupMediaByDate(items) else emptyList()

        _uiState.update {
            it.copy(
                isLoading = false,
                mediaList = items,
                dateGroups = groups,
                // Prune any selected items that no longer exist
                selectedIds = it.selectedIds.filter { id -> items.any { item -> item.id == id } }.toSet(),
                isSelectionMode = if (it.selectedIds.isEmpty()) false else it.isSelectionMode
            )
        }
    }

    fun toggleSelection(mediaId: Long) {
        _uiState.update { state ->
            val updated = state.selectedIds.toMutableSet()
            if (updated.contains(mediaId)) {
                updated.remove(mediaId)
            } else {
                updated.add(mediaId)
            }
            state.copy(
                selectedIds = updated,
                isSelectionMode = updated.isNotEmpty()
            )
        }
    }

    fun selectAll() {
        _uiState.update { state ->
            state.copy(
                selectedIds = state.mediaList.map { it.id }.toSet(),
                isSelectionMode = true
            )
        }
    }

    fun clearSelection() {
        _uiState.update {
            it.copy(
                selectedIds = emptySet(),
                isSelectionMode = false
            )
        }
    }

    fun toggleFavoriteSelected() {
        val selected = getSelectedItems()
        if (selected.isEmpty()) return

        viewModelScope.launch {
            val allFav = selected.all { it.isFavorite }
            repository.setFavorites(selected, !allFav)
            clearSelection()
        }
    }

    fun getSelectedItems(): List<MediaItem> {
        val ids = _uiState.value.selectedIds
        return _uiState.value.mediaList.filter { ids.contains(it.id) }
    }

    fun deleteSelected(activity: Activity, onPendingIntent: (IntentSenderRequest) -> Unit) {
        val selected = getSelectedItems()
        if (selected.isEmpty()) return

        val success = repository.deleteMediaItems(activity, selected) { request ->
            onPendingIntent(request)
        }
        if (success) {
            clearSelection()
            refresh()
        }
    }

    fun setSortOrder(sortOrder: SortOrder) {
        viewModelScope.launch {
            preferences.setSortOrder(sortOrder)
        }
    }

    fun setGridDensity(density: GridDensity) {
        viewModelScope.launch {
            preferences.setGridDensity(density)
        }
    }

    fun setGroupByDate(groupByDate: Boolean) {
        viewModelScope.launch {
            preferences.setGroupByDate(groupByDate)
        }
    }

    private data class PreferencesSnapshot(
        val sortOrder: SortOrder,
        val gridDensity: GridDensity,
        val groupByDate: Boolean,
        val favoriteIds: Set<Long>
    )

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val app = context.applicationContext as EziGalleryApp
                    return PhotosViewModel(app.repository, app.preferences, app) as T
                }
            }
    }
}

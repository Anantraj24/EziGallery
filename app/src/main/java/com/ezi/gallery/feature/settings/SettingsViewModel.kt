package com.ezi.gallery.feature.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ezi.gallery.EziGalleryApp
import com.ezi.gallery.core.model.GridDensity
import com.ezi.gallery.core.model.SortOrder
import com.ezi.gallery.core.model.ThemeMode
import com.ezi.gallery.core.preferences.AppPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferences: AppPreferences
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = preferences.themeModeFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ThemeMode.SYSTEM
    )

    val gridDensity: StateFlow<GridDensity> = preferences.gridDensityFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GridDensity.THREE
    )

    val sortOrder: StateFlow<SortOrder> = preferences.sortOrderFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SortOrder.DATE_DESC
    )

    val groupByDate: StateFlow<Boolean> = preferences.groupByDateFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            preferences.setThemeMode(mode)
        }
    }

    fun setGridDensity(density: GridDensity) {
        viewModelScope.launch {
            preferences.setGridDensity(density)
        }
    }

    fun setSortOrder(order: SortOrder) {
        viewModelScope.launch {
            preferences.setSortOrder(order)
        }
    }

    fun setGroupByDate(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setGroupByDate(enabled)
        }
    }

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val app = context.applicationContext as EziGalleryApp
                    return SettingsViewModel(app.preferences) as T
                }
            }
    }
}

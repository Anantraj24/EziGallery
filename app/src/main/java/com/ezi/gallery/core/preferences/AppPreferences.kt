package com.ezi.gallery.core.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ezi.gallery.core.model.GridDensity
import com.ezi.gallery.core.model.SortOrder
import com.ezi.gallery.core.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ezi_gallery_preferences")

class AppPreferences(private val context: Context) {

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val GRID_DENSITY = stringPreferencesKey("grid_density")
        val SORT_ORDER = stringPreferencesKey("sort_order")
        val GROUP_BY_DATE = booleanPreferencesKey("group_by_date")
    }

    val themeModeFlow: Flow<ThemeMode> = context.dataStore.data.map { preferences ->
        val name = preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name
        try {
            ThemeMode.valueOf(name)
        } catch (e: Exception) {
            ThemeMode.SYSTEM
        }
    }

    val gridDensityFlow: Flow<GridDensity> = context.dataStore.data.map { preferences ->
        val name = preferences[PreferencesKeys.GRID_DENSITY] ?: GridDensity.THREE.name
        try {
            GridDensity.valueOf(name)
        } catch (e: Exception) {
            GridDensity.THREE
        }
    }

    val sortOrderFlow: Flow<SortOrder> = context.dataStore.data.map { preferences ->
        val name = preferences[PreferencesKeys.SORT_ORDER] ?: SortOrder.DATE_DESC.name
        try {
            SortOrder.valueOf(name)
        } catch (e: Exception) {
            SortOrder.DATE_DESC
        }
    }

    val groupByDateFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.GROUP_BY_DATE] ?: true
    }

    suspend fun setThemeMode(themeMode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = themeMode.name
        }
    }

    suspend fun setGridDensity(density: GridDensity) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.GRID_DENSITY] = density.name
        }
    }

    suspend fun setSortOrder(sortOrder: SortOrder) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SORT_ORDER] = sortOrder.name
        }
    }

    suspend fun setGroupByDate(groupByDate: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.GROUP_BY_DATE] = groupByDate
        }
    }
}

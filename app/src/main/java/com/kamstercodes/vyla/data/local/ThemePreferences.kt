package com.kamstercodes.vyla.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_prefs")

class ThemePreferences(private val context: Context) {

    companion object {
        val SELECTED_ICON_PACK_ID = stringPreferencesKey("selected_icon_pack_id")
        val SELECTED_WALLPAPER_URL = stringPreferencesKey("selected_wallpaper_url")
        val SELECTED_WIDGET_TYPE = stringPreferencesKey("selected_widget_type")
        val THEME_MODE = stringPreferencesKey("theme_mode") // "light", "dark", "system"
        
        // Customization Keys
        val ACCENT_COLOR = stringPreferencesKey("accent_color") // Hex
        val TRANSPARENCY_ALPHA = floatPreferencesKey("transparency_alpha")
        val SHOW_APP_LABELS = booleanPreferencesKey("show_app_labels")
        val GRID_COLUMNS = intPreferencesKey("grid_columns")
        
        // New Customization Keys
        val ICON_SIZE = floatPreferencesKey("icon_size") // 40dp to 72dp
        val LABEL_SIZE = floatPreferencesKey("label_size") // 8sp to 14sp
        val USE_FLOWER_LAYOUT = booleanPreferencesKey("use_flower_layout")
    }

    val selectedIconPackId: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[SELECTED_ICON_PACK_ID] }

    val selectedWallpaperUrl: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[SELECTED_WALLPAPER_URL] }
        
    val selectedWidgetType: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[SELECTED_WIDGET_TYPE] }

    val themeMode: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[THEME_MODE] ?: "system" }

    val accentColor: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[ACCENT_COLOR] ?: "#00FFCC" }

    val transparencyAlpha: Flow<Float> = context.dataStore.data
        .map { preferences -> preferences[TRANSPARENCY_ALPHA] ?: 0.4f }

    val showAppLabels: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[SHOW_APP_LABELS] ?: true }

    val gridColumns: Flow<Int> = context.dataStore.data
        .map { preferences -> preferences[GRID_COLUMNS] ?: 4 }

    val iconSize: Flow<Float> = context.dataStore.data
        .map { preferences -> preferences[ICON_SIZE] ?: 56f }

    val labelSize: Flow<Float> = context.dataStore.data
        .map { preferences -> preferences[LABEL_SIZE] ?: 10f }

    val useFlowerLayout: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[USE_FLOWER_LAYOUT] ?: false }

    suspend fun saveIconPackId(id: String) {
        context.dataStore.edit { preferences -> preferences[SELECTED_ICON_PACK_ID] = id }
    }

    suspend fun saveWallpaperUrl(url: String) {
        context.dataStore.edit { preferences -> preferences[SELECTED_WALLPAPER_URL] = url }
    }

    suspend fun saveWidgetType(type: String) {
        context.dataStore.edit { preferences -> preferences[SELECTED_WIDGET_TYPE] = type }
    }

    suspend fun saveThemeMode(mode: String) {
        context.dataStore.edit { preferences -> preferences[THEME_MODE] = mode }
    }

    suspend fun saveAccentColor(colorHex: String) {
        context.dataStore.edit { preferences -> preferences[ACCENT_COLOR] = colorHex }
    }

    suspend fun saveTransparencyAlpha(alpha: Float) {
        context.dataStore.edit { preferences -> preferences[TRANSPARENCY_ALPHA] = alpha }
    }

    suspend fun saveShowAppLabels(show: Boolean) {
        context.dataStore.edit { preferences -> preferences[SHOW_APP_LABELS] = show }
    }

    suspend fun saveGridColumns(columns: Int) {
        context.dataStore.edit { preferences -> preferences[GRID_COLUMNS] = columns }
    }

    suspend fun saveIconSize(size: Float) {
        context.dataStore.edit { preferences -> preferences[ICON_SIZE] = size }
    }

    suspend fun saveLabelSize(size: Float) {
        context.dataStore.edit { preferences -> preferences[LABEL_SIZE] = size }
    }

    suspend fun saveUseFlowerLayout(use: Boolean) {
        context.dataStore.edit { preferences -> preferences[USE_FLOWER_LAYOUT] = use }
    }
}

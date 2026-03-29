package com.kamstercodes.vyla.ui.viewmodel

import android.content.pm.ApplicationInfo
import android.os.Build
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kamstercodes.vyla.data.LauncherRepository
import com.kamstercodes.vyla.data.model.AppInfo
import com.kamstercodes.vyla.data.model.IconPackInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppCategory(val title: String, val icon: ImageVector) {
    COMMUNICATION("Communication", Icons.AutoMirrored.Filled.Chat),
    INTERNET("Internet", Icons.Default.Language),
    GAMES("Games", Icons.Default.Games),
    MEDIA("Media", Icons.Default.MusicNote),
    UTILITIES("Utilities", Icons.Default.Build),
    SETTINGS("Settings", Icons.Default.Settings),
    OTHER("Other", Icons.Default.Category)
}

class LauncherViewModel(private val repository: LauncherRepository) : ViewModel() {
    private val _allApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val allApps: StateFlow<List<AppInfo>> = _allApps

    private val _availableIconPacks = MutableStateFlow<List<IconPackInfo>>(emptyList())
    val availableIconPacks: StateFlow<List<IconPackInfo>> = _availableIconPacks

    private val _selectedIconPack = MutableStateFlow<String?>(null)

    val categorizedApps: StateFlow<Map<AppCategory, List<AppInfo>>> = _allApps.map { apps ->
        apps.groupBy { app ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                when (app.category) {
                    ApplicationInfo.CATEGORY_GAME -> AppCategory.GAMES
                    ApplicationInfo.CATEGORY_AUDIO, ApplicationInfo.CATEGORY_VIDEO, ApplicationInfo.CATEGORY_IMAGE -> AppCategory.MEDIA
                    ApplicationInfo.CATEGORY_MAPS -> AppCategory.COMMUNICATION
                    ApplicationInfo.CATEGORY_PRODUCTIVITY -> AppCategory.UTILITIES
                    else -> inferCategory(app)
                }
            } else {
                inferCategory(app)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyMap())

    init {
        loadApps()
        loadIconPacks()
    }

    fun loadApps(iconPackPackage: String? = _selectedIconPack.value) {
        _selectedIconPack.value = iconPackPackage
        viewModelScope.launch {
            _allApps.value = repository.getInstalledApps(iconPackPackage)
        }
    }

    private fun loadIconPacks() {
        viewModelScope.launch {
            _availableIconPacks.value = repository.getAvailableIconPacks()
        }
    }

    private fun inferCategory(app: AppInfo): AppCategory {
        val label = app.label.lowercase()
        val pkg = app.packageName.lowercase()
        return when {
            pkg.contains("game") || pkg.contains("play") -> AppCategory.GAMES
            pkg.contains("music") || pkg.contains("video") || pkg.contains("player") || pkg.contains("gallery") -> AppCategory.MEDIA
            pkg.contains("chrome") || pkg.contains("browser") || pkg.contains("internet") -> AppCategory.INTERNET
            pkg.contains("message") || pkg.contains("chat") || pkg.contains("social") || pkg.contains("contacts") || pkg.contains("telephony") -> AppCategory.COMMUNICATION
            pkg.contains("setting") || pkg.contains("config") -> AppCategory.SETTINGS
            pkg.contains("tool") || pkg.contains("calc") || pkg.contains("clock") || pkg.contains("file") -> AppCategory.UTILITIES
            else -> AppCategory.OTHER
        }
    }

    fun launchApp(packageName: String) {
        repository.launchApp(packageName)
    }
}

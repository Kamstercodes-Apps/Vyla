package com.kamstercodes.vyla.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kamstercodes.vyla.data.LauncherRepository
import com.kamstercodes.vyla.data.model.AppInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LauncherViewModel(private val repository: LauncherRepository) : ViewModel() {

    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredApps: StateFlow<List<AppInfo>> = combine(_apps, _searchQuery) { apps, query ->
        if (query.isBlank()) {
            apps
        } else {
            apps.filter { it.label.contains(query, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        loadApps()
    }

    fun loadApps() {
        viewModelScope.launch {
            _apps.value = repository.getInstalledApps()
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun launchApp(packageName: String) {
        repository.launchApp(packageName)
    }
}

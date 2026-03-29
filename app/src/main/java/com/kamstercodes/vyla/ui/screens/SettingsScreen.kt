package com.kamstercodes.vyla.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kamstercodes.vyla.data.local.ThemePreferences
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToWallpapers: () -> Unit,
    onNavigateToIcons: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val themePreferences = remember { ThemePreferences(context) }
    
    val accentColorHex by themePreferences.accentColor.collectAsState(initial = "#00FFCC")
    val transparency by themePreferences.transparencyAlpha.collectAsState(initial = 0.4f)
    val iconSize by themePreferences.iconSize.collectAsState(initial = 56f)
    val labelSize by themePreferences.labelSize.collectAsState(initial = 10f)
    val showLabels by themePreferences.showAppLabels.collectAsState(initial = true)
    val useFlower by themePreferences.useFlowerLayout.collectAsState(initial = false)
    val columns by themePreferences.gridColumns.collectAsState(initial = 4)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("VYLA CUSTOMIZE", fontWeight = FontWeight.Black, letterSpacing = 2.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { SectionHeader("Visual Style") }
            
            item {
                SettingsCard(
                    title = "Wallpapers & Themes",
                    description = "Choose background and visual accent",
                    icon = Icons.Default.Wallpaper,
                    onClick = onNavigateToWallpapers
                )
            }

            item {
                SettingsCard(
                    title = "Icon Packs",
                    description = "Manage system and custom icons",
                    icon = Icons.Default.Palette,
                    onClick = onNavigateToIcons
                )
            }

            item { SectionHeader("Home Screen") }

            item {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("Home Layout", style = MaterialTheme.typography.labelLarge)
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                        FilterChip(
                            selected = !useFlower,
                            onClick = { scope.launch { themePreferences.saveUseFlowerLayout(false) } },
                            label = { Text("Grid") }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        FilterChip(
                            selected = useFlower,
                            onClick = { scope.launch { themePreferences.saveUseFlowerLayout(true) } },
                            label = { Text("Flower") }
                        )
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("Icon Size: ${iconSize.toInt()}dp", style = MaterialTheme.typography.labelLarge)
                    Slider(
                        value = iconSize,
                        onValueChange = { scope.launch { themePreferences.saveIconSize(it) } },
                        valueRange = 40f..72f
                    )
                }
            }

            item { SectionHeader("App Drawer") }

            item {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("Drawer Columns: $columns", style = MaterialTheme.typography.labelLarge)
                    Slider(
                        value = columns.toFloat(),
                        onValueChange = { scope.launch { themePreferences.saveGridColumns(it.toInt()) } },
                        valueRange = 3f..6f,
                        steps = 2
                    )
                }
            }

            item {
                ListItem(
                    headlineContent = { Text("Show Labels") },
                    trailingContent = {
                        Switch(checked = showLabels, onCheckedChange = { scope.launch { themePreferences.saveShowAppLabels(it) } })
                    }
                )
            }
            
            item {
                if (showLabels) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Label Size: ${labelSize.toInt()}sp", style = MaterialTheme.typography.labelLarge)
                        Slider(
                            value = labelSize,
                            onValueChange = { scope.launch { themePreferences.saveLabelSize(it) } },
                            valueRange = 8f..14f
                        )
                    }
                }
            }

            item { SectionHeader("Advanced") }

            item {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("UI Transparency: ${(transparency * 100).toInt()}%", style = MaterialTheme.typography.labelLarge)
                    Slider(
                        value = transparency,
                        onValueChange = { scope.launch { themePreferences.saveTransparencyAlpha(it) } },
                        valueRange = 0.05f..0.8f
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun SettingsCard(title: String, description: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

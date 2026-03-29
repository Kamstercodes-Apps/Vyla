package com.kamstercodes.vyla.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
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
import coil.compose.rememberAsyncImagePainter
import com.kamstercodes.vyla.data.LauncherRepository
import com.kamstercodes.vyla.data.local.ThemePreferences
import com.kamstercodes.vyla.data.model.IconPackInfo
import com.kamstercodes.vyla.ui.viewmodel.LauncherViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconPackLibraryScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val themePreferences = remember { ThemePreferences(context) }
    val repository = remember { LauncherRepository(context) }
    val viewModel = remember { LauncherViewModel(repository) }
    
    val iconPacks by viewModel.availableIconPacks.collectAsState()
    val selectedPackId by themePreferences.selectedIconPackId.collectAsState(initial = null)

    var visibleItems by remember { mutableStateOf(setOf<Int>()) }

    LaunchedEffect(iconPacks) {
        iconPacks.forEachIndexed { index, _ ->
            delay(50)
            visibleItems = visibleItems + index
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ICON PACKS", fontWeight = FontWeight.Black, letterSpacing = 2.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (iconPacks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No icon packs found", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        IconPackItem(
                            pack = IconPackInfo("none", "System Default", context.packageManager.getApplicationIcon(context.packageName)),
                            isSelected = selectedPackId == null || selectedPackId == "none",
                            onApply = {
                                scope.launch {
                                    themePreferences.saveIconPackId("none")
                                    viewModel.loadApps(null)
                                }
                            }
                        )
                    }
                    
                    itemsIndexed(iconPacks) { index, pack ->
                        AnimatedVisibility(
                            visible = index in visibleItems,
                            enter = fadeIn() + slideInHorizontally()
                        ) {
                            IconPackItem(
                                pack = pack,
                                isSelected = selectedPackId == pack.packageName,
                                onApply = {
                                    scope.launch {
                                        themePreferences.saveIconPackId(pack.packageName)
                                        viewModel.loadApps(pack.packageName)
                                        Toast.makeText(context, "Applied ${pack.label}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IconPackItem(pack: IconPackInfo, isSelected: Boolean, onApply: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onApply),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) 
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(pack.icon),
                contentDescription = null,
                modifier = Modifier.size(52.dp).clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = pack.label, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(text = pack.packageName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (isSelected) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

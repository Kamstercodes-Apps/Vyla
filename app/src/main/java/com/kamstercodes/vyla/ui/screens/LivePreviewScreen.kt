package com.kamstercodes.vyla.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kamstercodes.vyla.data.local.ThemePreferences
import com.kamstercodes.vyla.ui.components.BatteryWidget
import com.kamstercodes.vyla.ui.components.ClockWidget
import com.kamstercodes.vyla.ui.components.WeatherWidget
import com.kamstercodes.vyla.ui.theme.VylaTheme
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LivePreviewScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val themePreferences = remember { ThemePreferences(context) }
    
    val wallpaperUrl by themePreferences.selectedWallpaperUrl.collectAsState(initial = null)
    val widgetType by themePreferences.selectedWidgetType.collectAsState(initial = "clock")
    val iconPackId by themePreferences.selectedIconPackId.collectAsState(initial = null)

    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        visible = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Wallpaper Background with Fade In
        if (wallpaperUrl != null) {
            AsyncImage(
                model = wallpaperUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background))
        }

        // Overlay for better readability
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.1f)))

        // Home Screen Content Simulation
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = onBack,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Black.copy(alpha = 0.4f),
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Widget with Animation
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -it / 2 }, animationSpec = spring(stiffness = Spring.StiffnessLow))
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                    when (widgetType) {
                        "weather" -> WeatherWidget(modifier = Modifier.fillMaxWidth(0.9f))
                        "battery" -> BatteryWidget(modifier = Modifier.fillMaxWidth(0.9f))
                        else -> ClockWidget(modifier = Modifier.fillMaxWidth(0.9f))
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // App Icons Simulation with Animation
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }, animationSpec = spring(stiffness = Spring.StiffnessMediumLow))
            ) {
                MockAppGrid(iconPackId)
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            // Dock Simulation with Animation
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it }, animationSpec = spring(stiffness = Spring.StiffnessMedium))
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(88.dp)
                        .padding(horizontal = 8.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    color = Color.White.copy(alpha = 0.25f),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(4) {
                            MockAppIcon(iconPackId, size = 52.dp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.navigationBarsPadding())
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun MockAppGrid(iconPackId: String?) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier.fillMaxWidth().height(240.dp),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        userScrollEnabled = false
    ) {
        items(8) {
            MockAppIcon(iconPackId)
        }
    }
}

@Composable
fun MockAppIcon(iconPackId: String?, size: androidx.compose.ui.unit.Dp = 56.dp) {
    val iconColor = when (iconPackId) {
        "1" -> Color(0xFF00FFCC) // Neon
        "2" -> Color(0xFFFFB3BA) // Pastel
        "3" -> Color(0xFF333333) // Dark
        "4" -> Color(0xFFFF5722) // Retro
        else -> MaterialTheme.colorScheme.primary
    }

    Box(
        modifier = Modifier
            .size(size)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(iconColor.copy(alpha = 0.9f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = null,
            tint = if (iconPackId == "2") Color.Black else Color.White,
            modifier = Modifier.size(size / 2)
        )
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun LivePreviewScreenPreview() {
    VylaTheme {
        LivePreviewScreen(onBack = {})
    }
}

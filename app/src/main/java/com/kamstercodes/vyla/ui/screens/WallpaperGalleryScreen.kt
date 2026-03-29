package com.kamstercodes.vyla.ui.screens

import android.app.WallpaperManager
import android.graphics.drawable.BitmapDrawable
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.kamstercodes.vyla.data.local.ThemePreferences
import com.kamstercodes.vyla.data.remote.model.WallpaperDto
import com.kamstercodes.vyla.ui.theme.VylaTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallpaperGalleryScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val themePreferences = remember { ThemePreferences(context) }
    
    // Placeholder data
    val wallpapers = remember {
        listOf(
            WallpaperDto("1", "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?q=80&w=1000", "", "Abstract"),
            WallpaperDto("2", "https://images.unsplash.com/photo-1579546929518-9e396f3cc809?q=80&w=1000", "", "Vibrant"),
            WallpaperDto("3", "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?q=80&w=1000", "", "Dark"),
            WallpaperDto("4", "https://images.unsplash.com/photo-1557683316-973673baf926?q=80&w=1000", "", "Gradient"),
            WallpaperDto("5", "https://images.unsplash.com/photo-1558591710-4b4a1ae0f04d?q=80&w=1000", "", "Art"),
            WallpaperDto("6", "https://images.unsplash.com/photo-1614850523296-d8c1af93d400?q=80&w=1000", "", "Energy")
        )
    }

    var selectedWallpaper by remember { mutableStateOf<WallpaperDto?>(null) }
    var visibleItems by remember { mutableStateOf(setOf<Int>()) }

    LaunchedEffect(Unit) {
        wallpapers.forEachIndexed { index, _ ->
            delay(100)
            visibleItems = visibleItems + index
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wallpapers", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 80.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(innerPadding)
        ) {
            itemsIndexed(wallpapers) { index, wallpaper ->
                AnimatedVisibility(
                    visible = index in visibleItems,
                    enter = fadeIn() + scaleIn(initialScale = 0.8f, animationSpec = spring(stiffness = Spring.StiffnessLow))
                ) {
                    WallpaperItem(
                        wallpaper = wallpaper,
                        onClick = { selectedWallpaper = wallpaper }
                    )
                }
            }
        }
    }

    if (selectedWallpaper != null) {
        AlertDialog(
            onDismissRequest = { selectedWallpaper = null },
            title = { Text("Apply Wallpaper", fontWeight = FontWeight.Bold) },
            text = { Text("Set this high-energy wallpaper to your device?") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            themePreferences.saveWallpaperUrl(selectedWallpaper!!.url)
                            applyWallpaper(context, selectedWallpaper!!.url, WallpaperManager.FLAG_SYSTEM)
                            selectedWallpaper = null
                        }
                    },
                    shape = MaterialTheme.shapes.medium
                ) { Text("Home Screen") }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            themePreferences.saveWallpaperUrl(selectedWallpaper!!.url)
                            applyWallpaper(context, selectedWallpaper!!.url, WallpaperManager.FLAG_LOCK)
                            selectedWallpaper = null
                        }
                    },
                    shape = MaterialTheme.shapes.medium
                ) { Text("Lock Screen") }
            },
            shape = MaterialTheme.shapes.extraLarge
        )
    }
}

@Composable
fun WallpaperItem(wallpaper: WallpaperDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.7f),
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        AsyncImage(
            model = wallpaper.url,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

private suspend fun applyWallpaper(context: android.content.Context, url: String, flag: Int) {
    withContext(Dispatchers.IO) {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(url)
            .allowHardware(false)
            .build()

        val result = (loader.execute(request) as? SuccessResult)?.drawable
        val bitmap = (result as? BitmapDrawable)?.bitmap

        if (bitmap != null) {
            val wallpaperManager = WallpaperManager.getInstance(context)
            try {
                wallpaperManager.setBitmap(bitmap, null, true, flag)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Vyla Styler: Wallpaper Applied!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Vyla Styler: Failed to Apply", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun WallpaperGalleryScreenPreview() {
    VylaTheme {
        WallpaperGalleryScreen(onBack = {})
    }
}

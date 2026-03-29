package com.kamstercodes.vyla.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kamstercodes.vyla.data.local.ThemePreferences
import com.kamstercodes.vyla.data.remote.model.IconPackDto
import com.kamstercodes.vyla.ui.theme.VylaTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconPackLibraryScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val themePreferences = remember { ThemePreferences(context) }
    
    // Placeholder data
    val iconPacks = remember {
        listOf(
            IconPackDto("1", "Vyla Neon", "https://cdn.pixabay.com/photo/2017/01/31/15/12/alien-2024928_1280.png", "Vyla Team"),
            IconPackDto("2", "Material Pastel", "https://cdn.pixabay.com/photo/2016/03/31/18/36/android-1294454_1280.png", "Google Design"),
            IconPackDto("3", "Dark Minimal", "https://cdn.pixbash.com/photo/2018/05/08/21/29/android-3384003_1280.png", "Onyx Icons"), // Fixed typo in URL
            IconPackDto("4", "Retro Synth", "https://cdn.pixabay.com/photo/2016/11/18/11/17/android-1834164_1280.png", "Retro Wave")
        )
    }

    var visibleItems by remember { mutableStateOf(setOf<Int>()) }

    LaunchedEffect(Unit) {
        iconPacks.forEachIndexed { index, _ ->
            delay(80)
            visibleItems = visibleItems + index
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Icon Packs", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(innerPadding)
        ) {
            itemsIndexed(iconPacks) { index, pack ->
                AnimatedVisibility(
                    visible = index in visibleItems,
                    enter = fadeIn() + slideInHorizontally(initialOffsetX = { it / 4 }, animationSpec = spring(stiffness = Spring.StiffnessLow))
                ) {
                    IconPackItem(
                        pack = pack,
                        onApply = {
                            scope.launch {
                                themePreferences.saveIconPackId(pack.id)
                                Toast.makeText(context, "VYLA: ${pack.name} Selected!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun IconPackItem(pack: IconPackDto, onApply: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(72.dp),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ) {
                AsyncImage(
                    model = pack.iconUrl,
                    contentDescription = pack.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pack.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "by ${pack.author}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(
                onClick = onApply,
                shape = MaterialTheme.shapes.large,
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text("Select", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun IconPackLibraryScreenPreview() {
    VylaTheme {
        IconPackLibraryScreen(onBack = {})
    }
}

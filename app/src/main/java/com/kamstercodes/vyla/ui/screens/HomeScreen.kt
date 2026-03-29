package com.kamstercodes.vyla.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.kamstercodes.vyla.data.LauncherRepository
import com.kamstercodes.vyla.data.local.ThemePreferences
import com.kamstercodes.vyla.data.model.AppInfo
import com.kamstercodes.vyla.ui.components.BatteryWidget
import com.kamstercodes.vyla.ui.components.ClockWidget
import com.kamstercodes.vyla.ui.components.WeatherWidget
import com.kamstercodes.vyla.ui.viewmodel.AppCategory
import com.kamstercodes.vyla.ui.viewmodel.LauncherViewModel
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onNavigateToSettings: () -> Unit
) {
    val context = LocalContext.current
    val themePreferences = remember { ThemePreferences(context) }
    val repository = remember { LauncherRepository(context) }
    val viewModel = remember { LauncherViewModel(repository) }
    
    val categorizedApps by viewModel.categorizedApps.collectAsState()
    val allApps by viewModel.allApps.collectAsState()
    val wallpaperUrl by themePreferences.selectedWallpaperUrl.collectAsState(initial = null)
    val widgetType by themePreferences.selectedWidgetType.collectAsState(initial = "clock")
    
    val accentColorHex by themePreferences.accentColor.collectAsState(initial = "#00FFCC")
    val transparency by themePreferences.transparencyAlpha.collectAsState(initial = 0.4f)
    val iconSize by themePreferences.iconSize.collectAsState(initial = 56f)
    val labelSize by themePreferences.labelSize.collectAsState(initial = 10f)
    val useFlowerLayout by themePreferences.useFlowerLayout.collectAsState(initial = false)
    val gridColumns by themePreferences.gridColumns.collectAsState(initial = 4)
    val showLabels by themePreferences.showAppLabels.collectAsState(initial = true)
    val iconPackId by themePreferences.selectedIconPackId.collectAsState(initial = "none")

    val accentColor = remember(accentColorHex) { Color(android.graphics.Color.parseColor(accentColorHex)) }
    val pagerState = rememberPagerState(pageCount = { 2 })

    LaunchedEffect(iconPackId) {
        viewModel.loadApps(if (iconPackId == "none" || iconPackId == "1" || iconPackId == "2") null else iconPackId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
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

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> MainHomePage(
                    widgetType = widgetType,
                    onNavigateToSettings = onNavigateToSettings,
                    transparency = transparency,
                    accentColor = accentColor,
                    apps = allApps.take(if (useFlowerLayout) 7 else 9),
                    onAppClick = { viewModel.launchApp(it) },
                    iconSize = iconSize.dp,
                    useFlowerLayout = useFlowerLayout,
                    iconPackId = iconPackId
                )
                1 -> AppDrawerPage(
                    categorizedApps = categorizedApps,
                    onAppClick = { viewModel.launchApp(it) },
                    transparency = transparency,
                    accentColor = accentColor,
                    iconSize = iconSize.dp,
                    labelSize = labelSize,
                    gridColumns = gridColumns,
                    showLabels = showLabels,
                    iconPackId = iconPackId
                )
            }
        }
        
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.3f))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                repeat(2) { iteration ->
                    val active = pagerState.currentPage == iteration
                    val color = if (active) accentColor else Color.White.copy(alpha = 0.4f)
                    val width by animateFloatAsState(targetValue = if (active) 24f else 8f)
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(color)
                            .size(width = width.dp, height = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MainHomePage(
    widgetType: String?,
    onNavigateToSettings: () -> Unit,
    transparency: Float,
    accentColor: Color,
    apps: List<AppInfo>,
    onAppClick: (String) -> Unit,
    iconSize: Dp,
    useFlowerLayout: Boolean,
    iconPackId: String?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Surface(
                onClick = onNavigateToSettings,
                shape = CircleShape,
                color = Color.Black.copy(alpha = transparency),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
            val widgetModifier = Modifier.fillMaxWidth(0.92f).graphicsLayer { 
                shadowElevation = 8f
                shape = RoundedCornerShape(24.dp)
                clip = true
            }
            when (widgetType) {
                "weather" -> WeatherWidget(modifier = widgetModifier)
                "battery" -> BatteryWidget(modifier = widgetModifier)
                else -> ClockWidget(modifier = widgetModifier)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Box(modifier = Modifier.padding(bottom = 60.dp)) {
            if (useFlowerLayout && apps.isNotEmpty()) {
                FlowerLayout(apps, onAppClick, transparency, accentColor, iconSize, iconPackId)
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.width(300.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(apps) { app ->
                        AppIconSimple(app, onAppClick, transparency, accentColor, iconSize, iconPackId)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(60.dp))
    }
}

@Composable
fun FlowerLayout(
    apps: List<AppInfo>,
    onAppClick: (String) -> Unit,
    transparency: Float,
    accentColor: Color,
    iconSize: Dp,
    iconPackId: String?
) {
    Box(
        modifier = Modifier.size(300.dp),
        contentAlignment = Alignment.Center
    ) {
        if (apps.isNotEmpty()) {
            AppIconSimple(apps[0], onAppClick, transparency, accentColor, iconSize * 1.3f, iconPackId)
        }
        
        val surroundingApps = apps.drop(1).take(6)
        surroundingApps.forEachIndexed { index, app ->
            val angle = (index * (360f / surroundingApps.size) - 90f) * (PI / 180f)
            val radius = 100.dp
            
            Box(
                modifier = Modifier.offset(
                    x = (radius.value * cos(angle)).dp,
                    y = (radius.value * sin(angle)).dp
                )
            ) {
                AppIconSimple(app, onAppClick, transparency, accentColor, iconSize, iconPackId)
            }
        }
    }
}

@Composable
fun AppDrawerPage(
    categorizedApps: Map<AppCategory, List<AppInfo>>,
    onAppClick: (String) -> Unit,
    transparency: Float,
    accentColor: Color,
    iconSize: Dp,
    labelSize: Float,
    gridColumns: Int,
    showLabels: Boolean,
    iconPackId: String?
) {
    var selectedCategory by remember { mutableStateOf(AppCategory.COMMUNICATION) }
    var searchQuery by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .width(84.dp)
                .fillMaxHeight()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color.Black.copy(alpha = transparency + 0.3f), Color.Transparent)
                    )
                )
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppCategory.values().forEach { category ->
                val isSelected = selectedCategory == category
                val scale by animateFloatAsState(if (isSelected) 1.25f else 1f)
                
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .graphicsLayer { 
                            scaleX = scale
                            scaleY = scale
                        }
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) accentColor.copy(alpha = 0.3f) else Color.Transparent)
                        .clickable { selectedCategory = category },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = category.title,
                        tint = if (isSelected) accentColor else Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp)
                    .clip(RoundedCornerShape(12.dp)),
                placeholder = { Text("Search apps...", color = Color.White.copy(alpha = 0.5f)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White.copy(alpha = 0.5f)) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.1f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                    disabledContainerColor = Color.White.copy(alpha = 0.1f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )

            val appsInCategory = categorizedApps[selectedCategory] ?: emptyList()
            val filteredApps = if (searchQuery.isEmpty()) appsInCategory else {
                appsInCategory.filter { it.label.contains(searchQuery, ignoreCase = true) }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(gridColumns),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                items(filteredApps) { app ->
                    AppItemDrawer(app, onAppClick, transparency, iconSize, labelSize, showLabels, accentColor, iconPackId)
                }
            }
        }
    }
}

@Composable
fun AppIconSimple(
    app: AppInfo, 
    onClick: (String) -> Unit, 
    transparency: Float, 
    accentColor: Color, 
    size: Dp,
    iconPackId: String?
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick(app.packageName) }
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = transparency))
                .graphicsLayer { 
                    shadowElevation = 4f
                    shape = CircleShape
                    clip = true
                },
            contentAlignment = Alignment.Center
        ) {
            val isNeon = iconPackId == "1"
            
            Image(
                painter = rememberAsyncImagePainter(app.icon),
                contentDescription = null,
                modifier = Modifier.size(size * 0.7f),
                colorFilter = if (isNeon) ColorFilter.tint(accentColor.copy(alpha = 0.8f)) else null
            )
            
            if (isNeon) {
                Box(modifier = Modifier.fillMaxSize().blur(12.dp).alpha(0.2f).background(accentColor))
            }
        }
    }
}

@Composable
fun AppItemDrawer(
    app: AppInfo, 
    onClick: (String) -> Unit, 
    transparency: Float,
    iconSize: Dp,
    labelSize: Float,
    showLabels: Boolean,
    accentColor: Color,
    iconPackId: String?
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick(app.packageName) }.width(IntrinsicSize.Min)
    ) {
        Box(
            modifier = Modifier
                .size(iconSize)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = transparency / 1.5f)),
            contentAlignment = Alignment.Center
        ) {
            val isNeon = iconPackId == "1"
            
            Image(
                painter = rememberAsyncImagePainter(app.icon),
                contentDescription = null, 
                modifier = Modifier.size(iconSize * 0.65f),
                colorFilter = if (isNeon) ColorFilter.tint(accentColor) else null
            )
        }
        
        if (showLabels) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = app.label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                fontSize = labelSize.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp)
            )
        }
    }
}

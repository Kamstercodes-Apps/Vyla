package com.kamstercodes.vyla.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
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
import com.kamstercodes.vyla.ui.components.GlassBox
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
            Box(modifier = Modifier.fillMaxSize().background(Color.Black))
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
                    allApps = allApps,
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
        
        // Refined Page Indicator (iOS 16/17 style)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f))
                .padding(horizontal = 14.dp, vertical = 7.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                repeat(2) { iteration ->
                    val active = pagerState.currentPage == iteration
                    val indicatorWidth by animateFloatAsState(targetValue = if (active) 24f else 8f)
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (active) Color.White else Color.White.copy(alpha = 0.45f))
                            .size(width = indicatorWidth.dp, height = 7.dp)
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
            GlassBox(
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onNavigateToSettings() },
                shape = CircleShape,
                transparency = 0.2f
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White, modifier = Modifier.size(24.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
            when (widgetType) {
                "weather" -> WeatherWidget(modifier = Modifier.fillMaxWidth(0.92f))
                "battery" -> BatteryWidget(modifier = Modifier.fillMaxWidth(0.92f))
                else -> ClockWidget(modifier = Modifier.fillMaxWidth(0.92f))
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Box(modifier = Modifier.padding(bottom = 80.dp)) {
            if (useFlowerLayout && apps.isNotEmpty()) {
                FlowerLayout(apps, onAppClick, transparency, accentColor, iconSize, iconPackId)
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.width(320.dp),
                    horizontalArrangement = Arrangement.spacedBy(28.dp),
                    verticalArrangement = Arrangement.spacedBy(28.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(apps) { app ->
                        AppIconSimple(app, onAppClick, transparency, accentColor, iconSize, iconPackId)
                    }
                }
            }
        }
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
        modifier = Modifier.size(310.dp),
        contentAlignment = Alignment.Center
    ) {
        if (apps.isNotEmpty()) {
            AppIconSimple(apps[0], onAppClick, transparency, accentColor, iconSize * 1.4f, iconPackId)
        }
        
        val surroundingApps = apps.drop(1).take(6)
        surroundingApps.forEachIndexed { index, app ->
            val angle = (index * (360f / surroundingApps.size) - 90f) * (PI / 180f)
            val radius = 110.dp
            
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawerPage(
    categorizedApps: Map<AppCategory, List<AppInfo>>,
    allApps: List<AppInfo>,
    onAppClick: (String) -> Unit,
    transparency: Float,
    accentColor: Color,
    iconSize: Dp,
    labelSize: Float,
    gridColumns: Int,
    showLabels: Boolean,
    iconPackId: String?
) {
    var selectedCategory by remember { mutableStateOf<AppCategory?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Sidebar: Improved Visual Clarity & Organization
        Column(
            modifier = Modifier
                .width(86.dp)
                .fillMaxHeight()
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // "All Apps" / "Recents" Icon
            CategoryIconButton(
                isSelected = selectedCategory == null,
                icon = Icons.Default.Search,
                onClick = { selectedCategory = null },
                accentColor = Color.White
            )

            Divider(modifier = Modifier.width(40.dp).padding(vertical = 8.dp), color = Color.White.copy(alpha = 0.15f))

            AppCategory.values().forEach { category ->
                CategoryIconButton(
                    isSelected = selectedCategory == category,
                    icon = category.icon,
                    onClick = { selectedCategory = category },
                    accentColor = accentColor
                )
            }
        }

        // Main Drawer Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 16.dp)
        ) {
            // Search Bar: Cleaner & iOS-inspired
            GlassBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                transparency = 0.15f
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search Apps", color = Color.White.copy(alpha = 0.5f), style = MaterialTheme.typography.bodyMedium) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(22.dp)) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = accentColor
                    ),
                    singleLine = true
                )
            }

            // Category Header / Content Title
            Text(
                text = (selectedCategory?.title ?: if(searchQuery.isEmpty()) "Recent & Recommended" else "Search Results").uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.4f),
                        offset = Offset(0f, 2f),
                        blurRadius = 4f
                    )
                ),
                color = Color.White.copy(alpha = 0.7f),
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(bottom = 16.dp, start = 4.dp)
            )

            val displayApps = remember(selectedCategory, searchQuery, categorizedApps, allApps) {
                when {
                    searchQuery.isNotEmpty() -> allApps.filter { it.label.contains(searchQuery, ignoreCase = true) }
                    selectedCategory != null -> categorizedApps[selectedCategory] ?: emptyList()
                    else -> allApps // In a real app, this would be sorted by usage
                }
            }

            // Grid: Balanced aesthetics & smooth navigation
            LazyVerticalGrid(
                columns = GridCells.Fixed(gridColumns),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(28.dp),
                contentPadding = PaddingValues(bottom = 120.dp, top = 4.dp)
            ) {
                items(displayApps) { app ->
                    AppItemDrawer(app, onAppClick, transparency, iconSize, labelSize, showLabels, accentColor, iconPackId)
                }
            }
        }
    }
}

@Composable
fun CategoryIconButton(
    isSelected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    accentColor: Color
) {
    val scale by animateFloatAsState(if (isSelected) 1.25f else 1f)
    
    Box(
        modifier = Modifier
            .size(54.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(RoundedCornerShape(18.dp))
            .background(if (isSelected) Color.White.copy(alpha = 0.2f) else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) accentColor else Color.White.copy(alpha = 0.5f),
            modifier = Modifier.size(26.dp)
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(4.dp)
                    .height(24.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accentColor)
            )
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
        GlassBox(
            modifier = Modifier.size(size),
            shape = CircleShape,
            transparency = transparency,
            borderWidth = 1.5.dp
        ) {
            val isNeon = iconPackId == "1"
            Image(
                painter = rememberAsyncImagePainter(app.icon),
                contentDescription = null,
                modifier = Modifier.size(size * 0.72f),
                colorFilter = if (isNeon) ColorFilter.tint(accentColor.copy(alpha = 0.95f)) else null
            )
            if (isNeon) {
                Box(modifier = Modifier.fillMaxSize().blur(20.dp).alpha(0.3f).background(accentColor))
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
        modifier = Modifier
            .width(IntrinsicSize.Min)
            .clickable { onClick(app.packageName) }
    ) {
        GlassBox(
            modifier = Modifier.size(iconSize),
            shape = RoundedCornerShape(iconSize / 4f),
            transparency = transparency,
            borderWidth = 1.dp
        ) {
            val isNeon = iconPackId == "1"
            Image(
                painter = rememberAsyncImagePainter(app.icon),
                contentDescription = null, 
                modifier = Modifier.size(iconSize * 0.7f),
                colorFilter = if (isNeon) ColorFilter.tint(accentColor) else null
            )
        }
        
        if (showLabels) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = app.label,
                style = TextStyle(
                    fontSize = labelSize.sp,
                    fontWeight = FontWeight.SemiBold,
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.7f),
                        offset = Offset(0f, 2f),
                        blurRadius = 4f
                    )
                ),
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                lineHeight = (labelSize + 2).sp,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
            )
        }
    }
}

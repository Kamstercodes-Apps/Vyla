package com.kamstercodes.vyla.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kamstercodes.vyla.ui.screens.HomeScreen
import com.kamstercodes.vyla.ui.screens.IconPackLibraryScreen
import com.kamstercodes.vyla.ui.screens.LivePreviewScreen
import com.kamstercodes.vyla.ui.screens.SettingsScreen
import com.kamstercodes.vyla.ui.screens.WallpaperGalleryScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Settings : Screen("settings")
    object WallpaperGallery : Screen("wallpaper_gallery")
    object IconPackLibrary : Screen("icon_pack_library")
    object LivePreview : Screen("live_preview")
}

@Composable
fun VylaNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(500)
            ) + fadeIn(animationSpec = tween(500))
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(500)
            ) + fadeOut(animationSpec = tween(500))
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(500)
            ) + fadeIn(animationSpec = tween(500))
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(500)
            ) + fadeOut(animationSpec = tween(500))
        }
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateToWallpapers = { navController.navigate(Screen.WallpaperGallery.route) },
                onNavigateToIcons = { navController.navigate(Screen.IconPackLibrary.route) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.WallpaperGallery.route) {
            WallpaperGalleryScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.IconPackLibrary.route) {
            IconPackLibraryScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.LivePreview.route) {
            LivePreviewScreen(onBack = { navController.popBackStack() })
        }
    }
}

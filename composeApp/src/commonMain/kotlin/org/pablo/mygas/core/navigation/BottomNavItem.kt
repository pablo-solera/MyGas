package org.pablo.mygas.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector


sealed class BottomNavItem<T>(val route: T, val icon: ImageVector, val label: String) {
    data object Home : BottomNavItem<HomeGraph>(HomeGraph, Icons.Default.Home, "Home")
    data object Favorite : BottomNavItem<FavoriteGraph>(FavoriteGraph, Icons.Default.Favorite, "Favorite")
    data object Settings : BottomNavItem<SettingsGraph>(SettingsGraph, Icons.Default.Settings, "Settings")
}
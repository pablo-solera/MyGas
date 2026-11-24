package org.pablo.mygas.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.StringResource
import org.pablo.mygas.resources.Res
import org.pablo.mygas.resources.nav_favorites
import org.pablo.mygas.resources.nav_home
import org.pablo.mygas.resources.nav_map
import org.pablo.mygas.resources.nav_settings

sealed class BottomNavItem<T : Any>(val route: T, val icon: ImageVector, val label: StringResource) {
    data object Home : BottomNavItem<HomeGraph>(HomeGraph, Icons.Default.Home, Res.string.nav_home)
    data object Favorite :
        BottomNavItem<FavoriteGraph>(FavoriteGraph, Icons.Default.Favorite, Res.string.nav_favorites)

    data object Map : BottomNavItem<MapGraph>(MapGraph, Icons.Default.Map, Res.string.nav_map)
    data object Settings :
        BottomNavItem<SettingsGraph>(SettingsGraph, Icons.Default.Settings, Res.string.nav_settings)
}

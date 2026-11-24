package org.pablo.mygas.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector


sealed class BottomNavItem<T : Any>(val route: T, val icon: ImageVector, val label: String) {
    data object Home : BottomNavItem<HomeGraph>(HomeGraph, Icons.Default.Home, "Inicio")
    data object Favorite :
        BottomNavItem<FavoriteGraph>(FavoriteGraph, Icons.Default.Favorite, "Favoritos")

    data object Map : BottomNavItem<MapGraph>(MapGraph, Icons.Default.Map, "Mapa")
    data object Settings :
        BottomNavItem<SettingsGraph>(SettingsGraph, Icons.Default.Settings, "Ajustes")
}

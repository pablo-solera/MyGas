package org.pablo.mygas.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.Scaffold
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import org.pablo.mygas.ui.screens.GasStationDetailScreen
import org.pablo.mygas.ui.screens.GasolineraFavoriteScreen
import org.pablo.mygas.ui.screens.HomeScreen
import org.pablo.mygas.ui.screens.MapScreen
import org.pablo.mygas.ui.screens.SettingsScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    fun NavGraphBuilder.homeGraph() {
        navigation<HomeGraph>(startDestination = Home) {
            composable<Home> {
                HomeScreen(
                    onGasStationClick = { gasStation ->
                        navController.navigate(
                            GasStationDetail(gasStation.id)
                        )
                    },
                )
            }
            composable<GasStationDetail> { backStackEntry ->
                val gasStationId = backStackEntry.toRoute<GasStationDetail>()
                GasStationDetailScreen(
                    gasStationId = gasStationId.id,
                    onBack = { navController.navigateUp() }

                )


            }
        }
    }

    fun NavGraphBuilder.favoriteGraph() {
        navigation<FavoriteGraph>(startDestination = Favorite) {
            composable<Favorite> {
                GasolineraFavoriteScreen(
                    onGasStationClick = { gasStationId ->
                        navController.navigate(
                            GasStationDetail(gasStationId)
                        )
                    }
                )
            }
        }
    }

    fun NavGraphBuilder.settingsGraph() {
        navigation<SettingsGraph>(startDestination = Settings) {
            composable<Settings> {
                SettingsScreen()
            }
        }
    }
    fun NavGraphBuilder.mapGraph() {
        navigation<MapGraph>(startDestination = Map) {
            composable<Map> {
                MapScreen()
            }
        }
    }

    BoxWithConstraints {
        val compact = maxWidth < 600.dp
        Scaffold(
            bottomBar = { if (compact) BottomNavigationBar(navController = navController) },
        ) { innerPadding ->
            Row(modifier = Modifier.padding(innerPadding)) {
                if (!compact) AdaptiveNavigationRail(navController)
                NavHost(
                    navController = navController,
                    startDestination = HomeGraph,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                ) {
                    homeGraph()
                    favoriteGraph()
                    if (mapAvailable) mapGraph()
                    settingsGraph()
                }
            }
        }
    }
}

@Composable
private fun AdaptiveNavigationRail(navController: androidx.navigation.NavHostController) {
    val destination by navController.currentBackStackEntryAsState()
    NavigationRail {
        val items = buildList {
            add(BottomNavItem.Home)
            add(BottomNavItem.Favorite)
            if (mapAvailable) add(BottomNavItem.Map)
            add(BottomNavItem.Settings)
        }
        items.forEach { item ->
            val selected = destination?.destination?.hierarchy?.any {
                it.route == item.route::class.qualifiedName
            } == true
            NavigationRailItem(
                selected = selected,
                onClick = { navController.navigate(item.route) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
            )
        }
    }
}

package org.pablo.mygas.core.navigation

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import org.jetbrains.compose.resources.stringResource
import org.pablo.mygas.ui.screens.GasStationDetailScreen
import org.pablo.mygas.ui.screens.GasolineraFavoriteScreen
import org.pablo.mygas.ui.screens.HomeScreen
import org.pablo.mygas.ui.screens.MapScreen
import org.pablo.mygas.ui.screens.SettingsScreen

private val topLevelDestinations: List<BottomNavItem<*>> = buildList {
    add(BottomNavItem.Home)
    add(BottomNavItem.Favorite)
    if (mapAvailable) add(BottomNavItem.Map)
    add(BottomNavItem.Settings)
}

fun NavController.navigateTopLevel(route: Any) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

fun NavDestination?.isInGraph(item: BottomNavItem<*>): Boolean =
    this?.hierarchy?.any { it.route == item.route::class.qualifiedName } == true

@Composable
fun Navigation() {
    val navController = rememberNavController()

    BoxWithConstraints {
        val compact = maxWidth < 600.dp
        Scaffold(
            bottomBar = { if (compact) BottomNavigationBar(navController = navController) },
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
            ) {
                if (!compact) AdaptiveNavigationRail(navController)
                NavHost(
                    navController = navController,
                    startDestination = HomeGraph,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                ) {
                    appGraphs(navController)
                }
            }
        }
    }
}

private fun NavGraphBuilder.appGraphs(navController: NavHostController) {
    val openDetail: (Int) -> Unit = { id -> navController.navigate(GasStationDetail(id)) }

    navigation<HomeGraph>(startDestination = Home) {
        composable<Home> { HomeScreen(onGasStationClick = { openDetail(it.id) }) }
    }
    navigation<FavoriteGraph>(startDestination = Favorite) {
        composable<Favorite> { GasolineraFavoriteScreen(onGasStationClick = openDetail) }
    }
    if (mapAvailable) {
        navigation<MapGraph>(startDestination = Map) {
            composable<Map> { MapScreen(onGasStationClick = openDetail) }
        }
    }
    navigation<SettingsGraph>(startDestination = Settings) {
        composable<Settings> { SettingsScreen() }
    }
    composable<GasStationDetail> { backStackEntry ->
        GasStationDetailScreen(
            gasStationId = backStackEntry.toRoute<GasStationDetail>().id,
            onBack = { navController.navigateUp() },
        )
    }
}

@Composable
private fun AdaptiveNavigationRail(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    NavigationRail {
        topLevelDestinations.forEach { item ->
            NavigationRailItem(
                selected = backStackEntry?.destination.isInGraph(item),
                onClick = { navController.navigateTopLevel(item.route) },
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(stringResource(item.label)) },
            )
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    NavigationBar {
        topLevelDestinations.forEach { item ->
            NavigationBarItem(
                selected = backStackEntry?.destination.isInGraph(item),
                onClick = { navController.navigateTopLevel(item.route) },
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(stringResource(item.label)) },
            )
        }
    }
}

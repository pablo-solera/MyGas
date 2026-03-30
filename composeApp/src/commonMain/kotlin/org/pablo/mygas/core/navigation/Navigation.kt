package org.pablo.mygas.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = HomeGraph,
            Modifier.padding(innerPadding)
        ) {
            homeGraph()
            favoriteGraph()
            settingsGraph()
        }
    }
}
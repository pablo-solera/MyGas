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
import org.koin.compose.viewmodel.koinViewModel
import org.pablo.mygas.data.remote.ListaEESSPrecio
import org.pablo.mygas.ui.screens.GasStationDetailScreen
import org.pablo.mygas.ui.screens.GasolineraFavoriteScreen
import org.pablo.mygas.ui.screens.HomeScreen
import org.pablo.mygas.ui.screens.SettingsScreen
import org.pablo.mygas.ui.viewmodels.GasolineraDetailViewModel
import org.pablo.mygas.ui.viewmodels.GasolineraFavoriteViewModel
import org.pablo.mygas.ui.viewmodels.HomeViewModel

@Composable
fun Navigation() {
    val navController = rememberNavController()

    val homeViewModel = koinViewModel<HomeViewModel>()
    val detailViewModel = koinViewModel<GasolineraDetailViewModel>()
    val favoriteViewModel = koinViewModel<GasolineraFavoriteViewModel>()

    fun NavGraphBuilder.homeGraph() {
        navigation<HomeGraph>(startDestination = Home) {
            composable<Home> {
                HomeScreen(
                    onGasolineraClick = { gasStation ->
                        navController.navigate(
                            GasStationDetail(
                                gasStation.IDEESS,
                                gasStation.Dirección,
                                gasStation.Latitud,
                                gasStation.longitud,
                                gasStation.precioGasoleoA,
                                gasStation.precioGasolina95E5,
                                gasStation.rotulo,
                                gasStation.Horario
                            )
                        )
                    },
                    viewModel = homeViewModel,
                )
            }
            composable<GasStationDetail> { backStackEntry ->
                val gasStation = backStackEntry.toRoute<GasStationDetail>()
                GasStationDetailScreen(
                    gasolinera = ListaEESSPrecio(
                        cp = "",
                        Dirección = gasStation.location,
                        Horario = gasStation.horario,
                        IDCCAA = "",
                        IDEESS = gasStation.id,
                        IDMunicipio = "",
                        IDProvincia = "",
                        Latitud = gasStation.latitude,
                        Localidad = "",
                        longitud = gasStation.longitude,
                        Margen = "",
                        Municipio = "",
                        precioGasoleoA = gasStation.dieselPrice,
                        precioGasolina95E5 = gasStation.gasolinePrice,
                        Provincia = "",
                        Remisión = "",
                        rotulo = gasStation.rotulo
                    ),
                    onBack = { navController.navigateUp() },
                    viewModel = detailViewModel
                )
            }
        }
    }

    fun NavGraphBuilder.favoriteGraph() {
        navigation<FavoriteGraph>(startDestination = Favorite) {
            composable<Favorite> {
                GasolineraFavoriteScreen(favoriteViewModel)
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
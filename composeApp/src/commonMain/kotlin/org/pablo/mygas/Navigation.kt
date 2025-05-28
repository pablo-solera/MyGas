package org.pablo.mygas

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.pablo.mygas.data.remote.GasolinerasRepository
import org.pablo.mygas.data.remote.ListaEESSPrecio
import org.pablo.mygas.data.remote.MunicipioService
import org.pablo.mygas.domain.GetAllComunidadesAutonomasUseCase
import org.pablo.mygas.domain.GetAllGasolinerasByMunicipioUseCase
import org.pablo.mygas.domain.GetAllMunicipiosByProvinciaUseCase
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
    val client = remember {
        HttpClient {
            install(Logging) {
                level = LogLevel.ALL
            }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    val viewModel = viewModel {
        HomeViewModel(
            GetAllGasolinerasByMunicipioUseCase(GasolinerasRepository(MunicipioService(client))),
            GetAllMunicipiosByProvinciaUseCase(GasolinerasRepository(MunicipioService(client)))
        )
    }
    val detailViewModel =
        viewModel { GasolineraDetailViewModel(GasolinerasRepository(MunicipioService(client))) }

    val favoriteViewModel = viewModel {
        GasolineraFavoriteViewModel(
            GetAllComunidadesAutonomasUseCase(GasolinerasRepository(MunicipioService(client))),
            GetAllMunicipiosByProvinciaUseCase(GasolinerasRepository(MunicipioService(client))),
            GetAllGasolinerasByMunicipioUseCase(GasolinerasRepository(MunicipioService(client)))
        )
    }

    fun NavGraphBuilder.homeGraph() {
        navigation<HomeGraph>(startDestination = Home) {
            composable<Home> {
                HomeScreen(
                    onGasolineraClick = { gasStation ->
                        navController.navigate(
                            GasStationDetail(
                                gasStation.Dirección,
                                gasStation.Latitud,
                                gasStation.longitud,
                                gasStation.precioGasoleoA,
                                gasStation.precioGasolina95E5,
                                gasStation.rotulo
                            )
                        )
                    },
                    viewModel = viewModel,
                    bottomBar = {
                        BottomNavigationBar(navController = navController)
                    }
                )
            }
            composable<GasStationDetail> { backStackEntry ->
                val gasStation = backStackEntry.toRoute<GasStationDetail>()
                GasStationDetailScreen(
                    gasolinera = ListaEESSPrecio(
                        cp = "",
                        Dirección = gasStation.location,
                        Horario = "",
                        IDCCAA = "",
                        IDEESS = "",
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
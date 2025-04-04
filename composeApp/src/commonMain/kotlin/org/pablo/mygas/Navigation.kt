package org.pablo.mygas

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.pablo.mygas.data.GasolinerasRepository
import org.pablo.mygas.data.ListaEESSPrecio
import org.pablo.mygas.data.MunicipioService
import org.pablo.mygas.ui.screens.GasolineraDetailScreen
import org.pablo.mygas.ui.screens.GasolineraFavoriteScreen
import org.pablo.mygas.ui.screens.HomeScreen
import org.pablo.mygas.ui.viewmodels.GasolineraDetailViewModel
import org.pablo.mygas.ui.viewmodels.HomeViewModel

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val client = remember {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }
    val viewModel = viewModel { HomeViewModel(GasolinerasRepository(MunicipioService(client))) }
    val detailViewModel = viewModel { GasolineraDetailViewModel(GasolinerasRepository(MunicipioService(client))) }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        NavHost(navController, startDestination = BottomNavItem.Home.route, Modifier.padding(innerPadding)) {
            composable(BottomNavItem.Home.route) { HomeScreen(onGasolineraClick = { gasolinera ->
                navController.navigate("detail/${gasolinera.Dirección}/${gasolinera.Latitud}/${gasolinera.longitud}/${gasolinera.precioGasoleoA}/${gasolinera.precioGasolina95E5}/${gasolinera.rotulo}")
            },viewModel) }
            composable(BottomNavItem.Favorite.route) { GasolineraFavoriteScreen() }
            composable(route = "detail/{direccion}/{latitud}/{longitud}/{precioGasoleo}/{precioGasolina}/{rotulo}",
                arguments = listOf(
                    navArgument("direccion") { type = NavType.StringType },
                    navArgument("latitud") { type = NavType.StringType },
                    navArgument("longitud") { type = NavType.StringType },
                    navArgument("precioGasoleo") { type = NavType.StringType },
                    navArgument("precioGasolina") { type = NavType.StringType },
                    navArgument("rotulo") { type = NavType.StringType }
                )
            ) { backstackEntry ->
                val direccion = backstackEntry.arguments?.getString("direccion")
                val latitud = backstackEntry.arguments?.getString("latitud")
                val longitud = backstackEntry.arguments?.getString("longitud")
                val precioGasoleo = backstackEntry.arguments?.getString("precioGasoleo")
                val precioGasolina = backstackEntry.arguments?.getString("precioGasolina")
                val rotulo = backstackEntry.arguments?.getString("rotulo")
                GasolineraDetailScreen(
                    gasolinera = ListaEESSPrecio(
                        cp = "",
                        Dirección = direccion ?: "",
                        Horario = "",
                        IDCCAA = "",
                        IDEESS = "",
                        IDMunicipio = "",
                        IDProvincia = "",
                        Latitud = latitud ?: "",
                        Localidad = "",
                        longitud = longitud ?: "",
                        Margen = "",
                        Municipio = "",
                        precioGasoleoA = precioGasoleo ?: "",
                        precioGasolina95E5 = precioGasolina ?: "",
                        Provincia = "",
                        Remisión = "",
                        rotulo = rotulo ?: ""
                    ),
                    onBack = { navController.navigateUp() },
                    gasolineraDetailViewModel = detailViewModel
                )
            }
        }
    }
}
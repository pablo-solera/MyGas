package org.pablo.mygas.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.pablo.mygas.core.utils.GasStationImage
import org.pablo.mygas.core.utils.MapOpener
import org.pablo.mygas.ui.components.GasStationInfo
import org.pablo.mygas.ui.components.GasStationPrices
import org.pablo.mygas.ui.components.ErrorState
import org.pablo.mygas.ui.state.Loadable
import org.pablo.mygas.core.navigation.mapNavigationAvailable
import org.pablo.mygas.ui.viewmodels.GasolineraDetailViewModel
import org.pablo.mygas.domain.repositories.UserPreferencesRepository
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GasStationDetailScreen(
    gasStationId: Int,
    onBack: () -> Unit,
    viewModel: GasolineraDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val preferences by koinInject<UserPreferencesRepository>().preferences.collectAsStateWithLifecycle()

    LaunchedEffect(gasStationId) {
        viewModel.checkFavorite(gasStationId)
        viewModel.getGasStationById(gasStationId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        state.isFavorite?.let { isFavorite ->
                            if (isFavorite) {
                                viewModel.removeGasStationId(gasStationId)
                            } else {
                                viewModel.insertGasStationId(gasStationId)
                            }

                        }
                    }) {
                        state.isFavorite?.let { isFavorite ->

                            Icon(
                                imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                contentDescription = if (isFavorite) "Quitar de favoritas" else "Añadir a favoritas"
                            )

                        }


                    }
                }
            )
        },
        floatingActionButton = {
            if (mapNavigationAvailable) {
            FloatingActionButton(
                onClick = {
                    try {
                        state.gasStation?.let { gasStation ->
                            MapOpener().openMapActivity(
                                gasStation.location.latitude.toString(),
                                gasStation.location.longitude.toString()
                            )
                        }
                        //MapOpener().openMapActivity(gasolinera.Latitud, gasolinera.longitud)
                    } catch (_: Exception) {
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.LocationOn,
                    contentDescription = "Iniciar navegación" // Texto para accesibilidad
                )
            }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp, 8.dp)
                .verticalScroll(rememberScrollState())

        ) {

            when (val loadState = state.loadState) {
                Loadable.Loading -> Text("Cargando...")
                is Loadable.Failed -> ErrorState(loadState.error, { viewModel.retry(gasStationId) })
                is Loadable.Content -> {
                    val gasStation = loadState.value
                Image(
                    painterResource(
                        GasStationImage.getImage(
                            gasStation.brand.substringBefore(' ')
                        )
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(16.dp))
                        .size(75.dp)
                )
                Spacer(modifier = Modifier.padding(16.dp))
                GasStationInfo(gasStation)
                 GasStationPrices(gasStation, preferences.favoriteFuel, preferences.tankCapacityLiters)
                }
                else -> Unit
            }
        }
    }
}

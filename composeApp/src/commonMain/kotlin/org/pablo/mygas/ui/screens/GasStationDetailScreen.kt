package org.pablo.mygas.ui.screens

import GasStationPrices
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.pablo.mygas.core.utils.GasStationImage
import org.pablo.mygas.core.utils.MapOpener
import org.pablo.mygas.ui.components.GasStationInfo
import org.pablo.mygas.ui.viewmodels.GasolineraDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GasStationDetailScreen(
    gasStationId: Int,
    onBack: () -> Unit,
    viewModel: GasolineraDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Dispatchers.IO) {
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
                            contentDescription = "Back"
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
                                contentDescription = "Back"
                            )

                        }


                    }
                }
            )
        },
        floatingActionButton = {
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
                    } catch (e: Exception) {
                        println("Error $e")
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.LocationOn,
                    contentDescription = "Iniciar navegación" // Texto para accesibilidad
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp, 8.dp)

        ) {

            state.gasStation?.let { gasStation ->
                Image(
                    painterResource(
                        GasStationImage().getImage(
                            gasStation.brand.split(" ").first()
                        )
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(16.dp))
                        .size(75.dp)
                        .aspectRatio(1f)
                )
                Spacer(modifier = Modifier.padding(16.dp))
                GasStationInfo(gasStation)
                GasStationPrices(gasStation)
            }
        }
    }
}


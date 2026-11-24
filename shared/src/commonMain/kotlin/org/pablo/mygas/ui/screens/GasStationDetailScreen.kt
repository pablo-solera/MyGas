package org.pablo.mygas.ui.screens

import org.jetbrains.compose.resources.stringResource
import org.pablo.mygas.resources.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import org.pablo.mygas.ui.components.CircularLoader
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
import org.pablo.mygas.core.utils.MapLauncher
import org.pablo.mygas.ui.components.GasStationInfo
import org.pablo.mygas.ui.components.GasStationPrices
import org.pablo.mygas.ui.components.ErrorState
import org.pablo.mygas.ui.state.Loadable
import org.pablo.mygas.core.navigation.mapNavigationAvailable
import org.pablo.mygas.ui.viewmodels.GasolineraDetailViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GasStationDetailScreen(
    gasStationId: Int,
    onBack: () -> Unit,
    viewModel: GasolineraDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    val mapLauncher = koinInject<MapLauncher>()

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
                            contentDescription = stringResource(Res.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (state.isFavorite) {
                            viewModel.removeGasStationId(gasStationId)
                        } else {
                            viewModel.insertGasStationId(gasStationId)
                        }
                    }) {
                        Icon(
                            imageVector = if (state.isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                            contentDescription = stringResource(if (state.isFavorite) Res.string.remove_favorite else Res.string.add_favorite)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (mapNavigationAvailable) {
            FloatingActionButton(
                onClick = {
                    (state.loadState as? Loadable.Content)?.value?.let { gasStation ->
                        if (gasStation.hasValidLocation) {
                            mapLauncher.open(gasStation.location.latitude, gasStation.location.longitude)
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.LocationOn,
                    contentDescription = stringResource(Res.string.open_maps)
                )
            }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 96.dp)
        ) {

            when (val loadState = state.loadState) {
                Loadable.Loading -> CircularLoader(Modifier.height(240.dp))
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
                Spacer(modifier = Modifier.height(16.dp))
                GasStationInfo(gasStation)
                 GasStationPrices(gasStation, preferences.favoriteFuel, preferences.tankCapacityLiters)
                }
                else -> Unit
            }
        }
    }
}

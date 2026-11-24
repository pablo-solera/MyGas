package org.pablo.mygas.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.pablo.mygas.core.navigation.mapNavigationAvailable
import org.pablo.mygas.core.utils.GasStationImage
import org.pablo.mygas.core.utils.MapLauncher
import org.pablo.mygas.domain.models.GasStation
import org.pablo.mygas.domain.models.UserPreferences
import org.pablo.mygas.resources.*
import org.pablo.mygas.ui.components.CircularLoader
import org.pablo.mygas.ui.components.ErrorState
import org.pablo.mygas.ui.components.GasStationInfo
import org.pablo.mygas.ui.components.GasStationPrices
import org.pablo.mygas.ui.state.Loadable
import org.pablo.mygas.ui.viewmodels.GasolineraDetailViewModel

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
                    FavoriteToggle(
                        isFavorite = state.isFavorite,
                        onToggle = { toggleFavorite(viewModel, gasStationId, state.isFavorite) },
                    )
                }
            )
        },
        floatingActionButton = {
            if (mapNavigationAvailable) {
                FloatingActionButton(onClick = { (state.loadState as? Loadable.Content)?.value?.let(mapLauncher::openStation) }) {
                    Icon(
                        imageVector = Icons.Rounded.LocationOn,
                        contentDescription = stringResource(Res.string.open_maps)
                    )
                }
            }
        }
    ) { padding ->
        GasStationDetailBody(
            loadState = state.loadState,
            preferences = preferences,
            onRetry = { viewModel.retry(gasStationId) },
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 96.dp),
        )
    }
}

/** Detail shown beside the list on wide screens, without its own top bar. */
@Composable
fun GasStationDetailPane(
    gasStationId: Int,
    modifier: Modifier = Modifier,
    viewModel: GasolineraDetailViewModel = koinViewModel(key = "detail-pane-$gasStationId"),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    val mapLauncher = koinInject<MapLauncher>()

    LaunchedEffect(gasStationId) {
        viewModel.checkFavorite(gasStationId)
        viewModel.getGasStationById(gasStationId)
    }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FavoriteToggle(
                isFavorite = state.isFavorite,
                onToggle = { toggleFavorite(viewModel, gasStationId, state.isFavorite) },
            )
            if (mapNavigationAvailable) {
                OutlinedButton(onClick = { (state.loadState as? Loadable.Content)?.value?.let(mapLauncher::openStation) }) {
                    Icon(Icons.Rounded.LocationOn, contentDescription = null)
                    Text(stringResource(Res.string.station_go_google_maps), modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
        GasStationDetailBody(
            loadState = state.loadState,
            preferences = preferences,
            onRetry = { viewModel.retry(gasStationId) },
        )
    }
}

@Composable
private fun FavoriteToggle(isFavorite: Boolean, onToggle: () -> Unit) {
    IconButton(onClick = onToggle) {
        Icon(
            imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
            contentDescription = stringResource(
                if (isFavorite) Res.string.remove_favorite else Res.string.add_favorite
            )
        )
    }
}

private fun toggleFavorite(viewModel: GasolineraDetailViewModel, id: Int, isFavorite: Boolean) {
    if (isFavorite) viewModel.removeGasStationId(id) else viewModel.insertGasStationId(id)
}

private fun MapLauncher.openStation(station: GasStation) {
    if (station.hasValidLocation) open(station.location.latitude, station.location.longitude)
}

@Composable
private fun GasStationDetailBody(
    loadState: Loadable<GasStation>,
    preferences: UserPreferences,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        when (loadState) {
            Loadable.Loading -> CircularLoader(Modifier.height(240.dp))
            is Loadable.Failed -> ErrorState(loadState.error, onRetry)
            is Loadable.Content -> {
                val gasStation = loadState.value
                Image(
                    painterResource(GasStationImage.getImage(gasStation.brand.substringBefore(' '))),
                    contentDescription = null,
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(16.dp))
                        .size(75.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                GasStationInfo(gasStation)
                GasStationPrices(gasStation, preferences.favoriteFuel, preferences.tankCapacityLiters)
            }
            Loadable.Idle -> Unit
        }
    }
}

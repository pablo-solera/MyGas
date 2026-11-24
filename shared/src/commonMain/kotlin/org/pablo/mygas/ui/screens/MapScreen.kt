package org.pablo.mygas.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.pablo.mygas.core.utils.MapOpener
import org.pablo.mygas.domain.models.map.MapProperties
import org.pablo.mygas.resources.Res
import org.pablo.mygas.resources.error_network_generic
import org.pablo.mygas.resources.map_no_stations
import org.pablo.mygas.resources.map_search_here
import org.pablo.mygas.resources.map_stations_found
import org.pablo.mygas.resources.map_zoom_in
import org.pablo.mygas.resources.retry
import org.pablo.mygas.ui.components.Map
import org.pablo.mygas.ui.components.StationQuickSheet
import org.pablo.mygas.ui.viewmodels.MapViewModel

@Composable
fun MapScreen(
    onGasStationClick: (Int) -> Unit,
    viewModel: MapViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val properties = remember { MapProperties() }
    val snackbarHostState = remember { SnackbarHostState() }
    val mapOpener = remember { MapOpener() }
    val errorMessage = stringResource(Res.string.error_network_generic)
    val retryLabel = stringResource(Res.string.retry)

    LaunchedEffect(state.error) {
        if (state.error == null) return@LaunchedEffect
        val result = snackbarHostState.showSnackbar(errorMessage, actionLabel = retryLabel)
        viewModel.consumeError()
        if (result == SnackbarResult.ActionPerformed) viewModel.searchHere()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Map(
            modifier = Modifier.fillMaxSize(),
            state = state.map,
            properties = properties,
            onViewportChanged = viewModel::onViewportChanged,
            onMarkerClick = viewModel::onMarkerClick,
            onMapClick = { viewModel.dismissSelection() },
        )

        Column(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AnimatedVisibility(
                visible = state.canSearchHere && !state.isSearching,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                ElevatedButton(onClick = viewModel::searchHere) {
                    Icon(Icons.Rounded.Search, contentDescription = null)
                    Text(
                        text = stringResource(Res.string.map_search_here),
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
            val hint = when {
                state.zoomInRequired -> stringResource(Res.string.map_zoom_in)
                state.hasSearched && !state.isSearching && !state.canSearchHere ->
                    if (state.stationCount == 0) stringResource(Res.string.map_no_stations)
                    else stringResource(Res.string.map_stations_found, state.stationCount)
                else -> null
            }
            if (hint != null) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shadowElevation = 2.dp,
                ) {
                    Text(
                        text = hint,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }
            }
        }

        if (state.isSearching) {
            LinearProgressIndicator(modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth())
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }

    state.selectedStation?.let { station ->
        StationQuickSheet(
            station = station,
            preferences = state.preferences,
            onDismiss = viewModel::dismissSelection,
            onOpenDetail = {
                viewModel.dismissSelection()
                onGasStationClick(station.id)
            },
            onNavigate = {
                mapOpener.openMapActivity(
                    station.location.latitude.toString(),
                    station.location.longitude.toString(),
                )
            },
        )
    }
}

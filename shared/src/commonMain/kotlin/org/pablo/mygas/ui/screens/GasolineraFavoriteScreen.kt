package org.pablo.mygas.ui.screens

import org.jetbrains.compose.resources.stringResource
import org.pablo.mygas.resources.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import org.koin.compose.viewmodel.koinViewModel
import org.pablo.mygas.ui.components.CircularLoader
import org.pablo.mygas.ui.components.GasStationList
import org.pablo.mygas.ui.viewmodels.GasolineraFavoriteViewModel
import org.pablo.mygas.ui.components.NoGasStationsFound
import org.pablo.mygas.ui.components.ErrorState
import org.pablo.mygas.ui.state.Loadable


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GasolineraFavoriteScreen(
    onGasStationClick: (Int) -> Unit,
    viewModel: GasolineraFavoriteViewModel = koinViewModel()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.favorites_title)) },
                scrollBehavior = scrollBehavior

            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)

    ) { padding ->
        when (val loadState = state.loadState) {
            Loadable.Loading -> CircularLoader(Modifier.padding(padding))
            is Loadable.Failed -> ErrorState(
                error = loadState.error,
                onRetry = viewModel::retry,
                modifier = Modifier.padding(padding),
            )
            is Loadable.Content -> if (loadState.value.isEmpty()) NoGasStationsFound(
                title = stringResource(Res.string.no_favorites),
                message = stringResource(Res.string.no_favorites_hint),
                modifier = Modifier.padding(padding),
            ) else PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = viewModel::refresh,
                modifier = Modifier.padding(padding).padding(horizontal = 20.dp),
            ) {
                GasStationList(
                    gasStations = loadState.value,
                    favoriteFuel = preferences.favoriteFuel,
                    tankCapacityLiters = preferences.tankCapacityLiters,
                    onClick = { onGasStationClick(it.id) }
                )
            }
            else -> Unit
        }
    }
}

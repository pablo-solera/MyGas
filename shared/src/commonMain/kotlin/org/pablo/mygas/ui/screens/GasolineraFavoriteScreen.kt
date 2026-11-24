package org.pablo.mygas.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import org.pablo.mygas.resources.Res
import org.pablo.mygas.resources.app_name
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.pablo.mygas.ui.components.GasStationList
import org.pablo.mygas.ui.viewmodels.GasolineraFavoriteViewModel
import org.pablo.mygas.domain.repositories.UserPreferencesRepository
import org.pablo.mygas.domain.models.priceFor
import org.koin.compose.koinInject
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
    val preferences by koinInject<UserPreferencesRepository>().preferences.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tus favoritas") },
                scrollBehavior = scrollBehavior

            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)

    ) { padding ->
        when (val loadState = state.loadState) {
            Loadable.Loading -> Text("Cargando favoritas...", modifier = Modifier.padding(padding))
            is Loadable.Failed -> ErrorState(
                error = loadState.error,
                onRetry = viewModel::retry,
                modifier = Modifier.padding(padding),
            )
            is Loadable.Content -> if (loadState.value.isEmpty()) NoGasStationsFound(
                title = "Aún no tienes favoritas",
                message = "Guarda una gasolinera para verla aquí.",
                modifier = Modifier.padding(padding),
            ) else GasStationList(
                modifier = Modifier.padding(padding),
                gasStations = loadState.value,
                favoriteFuel = preferences.favoriteFuel,
                tankCapacityLiters = preferences.tankCapacityLiters,
                onClick = { onGasStationClick(it.id) }
            )
            else -> Unit
        }
    }
}

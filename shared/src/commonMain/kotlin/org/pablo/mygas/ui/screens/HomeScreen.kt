package org.pablo.mygas.ui.screens

import org.jetbrains.compose.resources.stringResource
import org.pablo.mygas.resources.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import org.pablo.mygas.domain.models.Provincia
import org.pablo.mygas.domain.models.GasStation
import org.pablo.mygas.ui.components.CustomDropDownMenu
import org.pablo.mygas.ui.components.GasStationList
import org.pablo.mygas.ui.components.GasStationSkeletonList
import org.pablo.mygas.ui.components.NoGasStationsFound
import org.pablo.mygas.ui.components.ErrorState
import org.pablo.mygas.ui.state.Loadable
import org.pablo.mygas.ui.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onGasStationClick: (GasStation) -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.home_title)) },
                scrollBehavior = scrollBehavior

            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)

    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)

        ) {
            Text(
                text = stringResource(Res.string.home_subtitle),
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Start),
            )
            CustomDropDownMenu(
                itemList = Provincia.entries,
                selectedText = state.provincia.displayName,
                label = stringResource(Res.string.select_province),
                itemText = { it.displayName },
                onValueChange = { viewModel.selectProvincia(it) }
            )
            when (val municipiosState = state.municipiosState) {
                is Loadable.Content -> CustomDropDownMenu(
                    itemList = municipiosState.value,
                    selectedText = state.municipio,
                    label = stringResource(Res.string.select_town),
                    itemText = { municipio -> municipio.name },
                    onValueChange = viewModel::selectMunicipio
                )
                is Loadable.Failed -> ErrorState(municipiosState.error, viewModel::retryMunicipios)
                else -> Unit
            }
            when (val gasStationsState = state.gasStationsState) {
                Loadable.Loading -> GasStationSkeletonList(modifier = Modifier.weight(1f))
                is Loadable.Failed -> ErrorState(
                    error = gasStationsState.error,
                    onRetry = viewModel::retryGasolineras,
                    modifier = Modifier.weight(1f),
                )
                is Loadable.Content -> if (gasStationsState.value.isEmpty()) {
                    NoGasStationsFound()
                } else {
                    GasStationList(
                        modifier = Modifier.weight(1f),
                        gasStations = gasStationsState.value,
                        favoriteFuel = preferences.favoriteFuel,
                        tankCapacityLiters = preferences.tankCapacityLiters,
                        onClick = { onGasStationClick(it) }
                    )
                }
                else -> Unit
            }
        }
    }
}

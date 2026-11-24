package org.pablo.mygas.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.pablo.mygas.domain.models.GasStation
import org.pablo.mygas.domain.models.Provincia
import org.pablo.mygas.resources.*
import org.pablo.mygas.ui.components.CustomDropDownMenu
import org.pablo.mygas.ui.components.ErrorState
import org.pablo.mygas.ui.components.GasStationList
import org.pablo.mygas.ui.components.GasStationSkeletonList
import org.pablo.mygas.ui.components.NoGasStationsFound
import org.pablo.mygas.ui.state.Loadable
import org.pablo.mygas.ui.viewmodels.HomeViewModel

private val WIDE_LAYOUT_MIN_WIDTH = 840.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onGasStationClick: (GasStation) -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    var selectedStationId by rememberSaveable { mutableStateOf<Int?>(null) }

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
        BoxWithConstraints(modifier = Modifier.padding(padding).fillMaxSize()) {
            val wide = maxWidth >= WIDE_LAYOUT_MIN_WIDTH

            Row(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .then(if (wide) Modifier.width(460.dp) else Modifier.weight(1f))
                        .fillMaxHeight()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.home_subtitle),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Start),
                    )
                    CustomDropDownMenu(
                        itemList = Provincia.entries,
                        selectedText = state.provincia.displayName,
                        label = stringResource(Res.string.select_province),
                        itemText = { it.displayName },
                        onValueChange = {
                            selectedStationId = null
                            viewModel.selectProvincia(it)
                        }
                    )
                    when (val municipiosState = state.municipiosState) {
                        is Loadable.Content -> CustomDropDownMenu(
                            itemList = municipiosState.value,
                            selectedText = state.municipio,
                            label = stringResource(Res.string.select_town),
                            itemText = { municipio -> municipio.name },
                            onValueChange = {
                                selectedStationId = null
                                viewModel.selectMunicipio(it)
                            }
                        )
                        is Loadable.Failed -> ErrorState(municipiosState.error, viewModel::retryMunicipios)
                        Loadable.Loading -> LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        Loadable.Idle -> Unit
                    }
                    when (val gasStationsState = state.gasStationsState) {
                        Loadable.Loading -> GasStationSkeletonList(modifier = Modifier.weight(1f))
                        is Loadable.Failed -> ErrorState(
                            error = gasStationsState.error,
                            onRetry = viewModel::retryGasolineras,
                            modifier = Modifier.weight(1f),
                        )
                        is Loadable.Content -> if (gasStationsState.value.isEmpty()) {
                            NoGasStationsFound(modifier = Modifier.weight(1f))
                        } else {
                            PullToRefreshBox(
                                isRefreshing = state.isRefreshing,
                                onRefresh = viewModel::refreshGasolineras,
                                modifier = Modifier.weight(1f),
                            ) {
                                GasStationList(
                                    gasStations = gasStationsState.value,
                                    favoriteFuel = preferences.favoriteFuel,
                                    tankCapacityLiters = preferences.tankCapacityLiters,
                                    onClick = { station ->
                                        if (wide) selectedStationId = station.id else onGasStationClick(station)
                                    }
                                )
                            }
                        }
                        Loadable.Idle -> if (state.municipios != null) {
                            NoGasStationsFound(
                                title = stringResource(Res.string.home_pick_municipality),
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }

                if (wide) {
                    VerticalDivider()
                    val selectedId = selectedStationId
                    if (selectedId != null) {
                        GasStationDetailPane(
                            gasStationId = selectedId,
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                        )
                    } else {
                        NoGasStationsFound(
                            title = stringResource(Res.string.station_select_hint),
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                        )
                    }
                }
            }
        }
    }
}

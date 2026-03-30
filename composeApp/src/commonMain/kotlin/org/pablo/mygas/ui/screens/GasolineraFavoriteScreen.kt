package org.pablo.mygas.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import mygas.composeapp.generated.resources.Res
import mygas.composeapp.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.pablo.mygas.ui.components.GasStationList
import org.pablo.mygas.ui.viewmodels.GasolineraFavoriteViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GasolineraFavoriteScreen(
    onGasStationClick: (Int) -> Unit,
    viewModel: GasolineraFavoriteViewModel = koinViewModel()
) {

    val state by viewModel.state.collectAsState()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gasolineras favoritas") },
                scrollBehavior = scrollBehavior

            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)

    ) { padding ->
        state.gasStations?.let { gasStations ->
            GasStationList(
                modifier = Modifier.padding(padding),
                gasStations = gasStations,
                onClick = { onGasStationClick(it.id) }
            )
        }
    }
}

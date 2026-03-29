package org.pablo.mygas.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.count
import org.koin.compose.viewmodel.koinViewModel
import org.pablo.mygas.ui.viewmodels.GasolineraFavoriteViewModel


@Composable
fun GasolineraFavoriteScreen(
    viewModel: GasolineraFavoriteViewModel = koinViewModel()
) {

    val state by viewModel.state.collectAsState()

    Text(text = "Gasolineras favoritas", modifier = Modifier.fillMaxSize())
    LazyColumn(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.fillMaxSize()
    ) {


        items(state.gasStations) { gasStation ->
            ListItem(
                headlineContent = { Text(text = "Gasolinera ID: ${gasStation.name}") },
                trailingContent = {Text(text = "Precio: ${gasStation.fuels.gasoline95.price} €/l") },
                modifier = Modifier.fillMaxSize()
            )
        }


    }

}

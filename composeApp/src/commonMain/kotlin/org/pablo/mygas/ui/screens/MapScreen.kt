package org.pablo.mygas.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.rounded.NearMe
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel
import org.pablo.mygas.domain.models.map.MapProperties
import org.pablo.mygas.domain.models.map.MapState
import org.pablo.mygas.ui.components.Map
import org.pablo.mygas.ui.viewmodels.MapViewModel

@Composable
fun MapScreen(
    viewmodel: MapViewModel = koinViewModel()
) {
    val state by viewmodel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(onClick = {  }) {
                Icon(Icons.Rounded.NearMe, contentDescription = "location")
            }
        }
    ) { innerPadding ->
        Map(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            state = MapState(
                markers = state.markers,
            ),
            properties = MapProperties()
        )
    }

}
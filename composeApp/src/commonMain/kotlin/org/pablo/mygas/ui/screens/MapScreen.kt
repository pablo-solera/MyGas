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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel
import org.pablo.mygas.domain.models.map.MapProperties
import org.pablo.mygas.ui.components.Map
import org.pablo.mygas.ui.viewmodels.MapViewModel
import org.pablo.mygas.ui.components.ErrorState
import org.pablo.mygas.ui.state.Loadable

@Composable
fun MapScreen(
    viewmodel: MapViewModel = koinViewModel()
) {
    val state by viewmodel.uiState.collectAsStateWithLifecycle()
    val properties = remember { MapProperties() }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                scope.launch { snackbarHostState.showSnackbar("La ubicación estará disponible próximamente") }
            }) {
                Icon(Icons.Rounded.NearMe, contentDescription = "location")
            }
        }
    ) { innerPadding ->
        if (state.markersState is Loadable.Failed) {
            ErrorState(
                error = (state.markersState as Loadable.Failed).error,
                onRetry = viewmodel::retry,
                modifier = Modifier.padding(innerPadding),
            )
        } else Map(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            state = state,
            properties = properties
        )
    }

}

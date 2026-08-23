package org.pablo.mygas.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.pablo.mygas.domain.models.FuelType
import org.pablo.mygas.domain.models.displayName
import org.pablo.mygas.ui.viewmodels.SettingsViewModel

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val preferences by viewModel.preferences.collectAsState()
    var capacity by remember(preferences.tankCapacityLiters) {
        mutableStateOf(preferences.tankCapacityLiters?.toString().orEmpty())
    }
    var expanded by remember { mutableStateOf(false) }
    var capacityError by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Ajustes") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text("Personaliza tus precios", style = androidx.compose.material3.MaterialTheme.typography.headlineSmall)
            Text(
                "Elige el combustible que quieres ver primero y calcula el coste de llenar tu depósito.",
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Combustible favorito", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                androidx.compose.foundation.layout.Box {
                    OutlinedTextField(
                        value = preferences.favoriteFuel.displayName,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = { TextButton(onClick = { expanded = true }) { Text("Cambiar") } },
                    )
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        FuelType.entries.forEach { fuel ->
                            DropdownMenuItem(
                                text = { Text(fuel.displayName) },
                                onClick = {
                                    viewModel.setFavoriteFuel(fuel)
                                    expanded = false
                                },
                                leadingIcon = if (fuel == preferences.favoriteFuel) {
                                    { Icon(Icons.Default.Check, contentDescription = null) }
                                } else null,
                            )
                        }
                    }
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Capacidad del depósito", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = capacity,
                    onValueChange = { capacity = it; capacityError = false },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = capacityError,
                    suffix = { Text("litros") },
                    supportingText = if (capacityError) {
                        { Text("Introduce un valor entre 1 y 200 litros") }
                    } else null,
                )
            }
            Button(
                onClick = {
                    val saved = viewModel.saveTankCapacity(capacity)
                    capacityError = !saved
                    if (saved) scope.launch { snackbarHostState.showSnackbar("Cambios guardados") }
                },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Guardar cambios") }
        }
    }
}

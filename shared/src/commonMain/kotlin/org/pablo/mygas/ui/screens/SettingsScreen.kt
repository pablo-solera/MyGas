package org.pablo.mygas.ui.screens

import org.jetbrains.compose.resources.stringResource
import org.pablo.mygas.resources.*
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import org.pablo.mygas.core.utils.formatDecimal
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
import org.pablo.mygas.domain.models.UserPreferences
import org.pablo.mygas.ui.components.label
import org.pablo.mygas.ui.viewmodels.SettingsViewModel

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    var capacity by rememberSaveable {
        mutableStateOf(preferences.tankCapacityLiters?.let { formatDecimal(it, 1).removeSuffix(",0") }.orEmpty())
    }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var capacityError by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val savedMessage = stringResource(Res.string.saved)
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(Res.string.settings_title)) }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).imePadding().verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(stringResource(Res.string.settings_heading), style = androidx.compose.material3.MaterialTheme.typography.headlineSmall)
            Text(
                stringResource(Res.string.settings_description),
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(Res.string.favorite_fuel), style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                androidx.compose.foundation.layout.Box {
                    OutlinedTextField(
                        value = preferences.favoriteFuel.label(),
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = { TextButton(onClick = { expanded = true }) { Text(stringResource(Res.string.change)) } },
                    )
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        FuelType.entries.forEach { fuel ->
                            DropdownMenuItem(
                                text = { Text(fuel.label()) },
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
                Text(stringResource(Res.string.tank_capacity), style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = capacity,
                    onValueChange = { capacity = it; capacityError = false },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                    isError = capacityError,
                    suffix = { Text(stringResource(Res.string.liters)) },
                    supportingText = if (capacityError) {
                        { Text(stringResource(Res.string.tank_capacity_error, UserPreferences.MIN_TANK_CAPACITY.toInt(), UserPreferences.MAX_TANK_CAPACITY.toInt())) }
                    } else null,
                )
            }
            Button(
                onClick = {
                    val saved = viewModel.saveTankCapacity(capacity)
                    capacityError = !saved
                    if (saved) scope.launch { snackbarHostState.showSnackbar(savedMessage) }
                },
                modifier = Modifier.fillMaxWidth(),
            ) { Text(stringResource(Res.string.save)) }
        }
    }
}

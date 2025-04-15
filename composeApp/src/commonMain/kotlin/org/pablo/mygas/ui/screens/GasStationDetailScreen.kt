package org.pablo.mygas.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import mygas.composeapp.generated.resources.Res
import mygas.composeapp.generated.resources.gasolinera
import org.jetbrains.compose.resources.painterResource
import org.pablo.mygas.data.ListaEESSPrecio
import org.pablo.mygas.ui.viewmodels.GasolineraDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GasStationDetailScreen(
    gasolinera: ListaEESSPrecio,
    onBack: () -> Unit,
    viewModel: GasolineraDetailViewModel
) {

    Screen {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(gasolinera.rotulo) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                imageVector = Icons.Default.FavoriteBorder,
                                contentDescription = "Back"
                            )


                        }
                    }
                )

            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Image(
                    painterResource(Res.drawable.gasolinera), contentDescription = null,
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(16.dp))
                        .size(75.dp)
                        .aspectRatio(1f)
                )
                Spacer(modifier = Modifier.padding(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Gasóleo",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = gasolinera.precioGasoleoA,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    Spacer(modifier = Modifier.padding(8.dp))
                    Column {
                        Text(
                            text = "Gasolina",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = gasolinera.precioGasolina95E5,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
                Spacer(modifier = Modifier.padding(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Dirección",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = gasolinera.Dirección,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Back"
                            )
                        }
                    }
            }

        }
    }
}

package org.pablo.mygas.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.ktor.http.ContentType
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.pablo.mygas.data.remote.ListaEESSPrecio
import org.pablo.mygas.ui.viewmodels.GasolineraDetailViewModel
import org.pablo.mygas.utils.GasStationImage
import org.pablo.mygas.utils.MapOpener

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GasStationDetailScreen(
    gasolinera: ListaEESSPrecio,
    onBack: () -> Unit,
    viewModel: GasolineraDetailViewModel = koinViewModel()
) {


    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    try {
                        MapOpener().openMapActivity(gasolinera.Latitud, gasolinera.longitud)
                    } catch (e: Exception) {
                        println("Error $e")
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.LocationOn,
                    contentDescription = "Iniciar navegación" // Texto para accesibilidad
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp, 32.dp)

        ) {
            Image(
                painterResource(GasStationImage().getImage(gasolinera.rotulo.split(" ").first())),
                contentDescription = null,
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(16.dp))
                    .size(75.dp)
                    .aspectRatio(1f)
            )
            Spacer(modifier = Modifier.padding(16.dp))
            GasStationInfo(gasolinera)
            GasStationPrices(gasolinera)
        }
    }
}

@Composable
fun GasStationInfo(
    gasolinera: ListaEESSPrecio
) {
    Column {
        Text(gasolinera.rotulo, style = MaterialTheme.typography.titleLarge)
        Text(
            gasolinera.Dirección,
            style = MaterialTheme.typography.bodyMedium,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Spacer(Modifier.height(16.dp))
        Text("Horario", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(gasolinera.Horario, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun GasStationPrices(
    gasolinera: ListaEESSPrecio
) {
    Column {
        Text("Precios", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Gasóleo A",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${gasolinera.precioGasoleoA}€",
                style = MaterialTheme.typography.titleLarge
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Gasolina 95",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${gasolinera.precioGasolina95E5}€",
                style = MaterialTheme.typography.titleLarge
            )
        }

    }
}


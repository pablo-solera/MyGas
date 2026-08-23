package org.pablo.mygas.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.pablo.mygas.domain.models.GasStation
import org.pablo.mygas.domain.models.FuelType
import org.pablo.mygas.domain.models.displayName
import org.pablo.mygas.domain.models.priceFor

@Composable
fun GasStationPrices(
    gasStation: GasStation,
    favoriteFuel: FuelType = FuelType.GASOLINE_95,
    tankCapacityLiters: Double? = null,
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
                text = favoriteFuel.displayName,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = gasStation.fuels.priceFor(favoriteFuel).price?.let { "$it €/l" } ?: "Sin datos",
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
                text = "Llenar el depósito",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = tankCapacityLiters?.let { liters ->
                    gasStation.fuels.priceFor(favoriteFuel).price?.let { "${it * liters} €" } ?: "Sin datos"
                } ?: "Configura la capacidad en Ajustes",
                style = MaterialTheme.typography.titleLarge
            )
        }

    }
}

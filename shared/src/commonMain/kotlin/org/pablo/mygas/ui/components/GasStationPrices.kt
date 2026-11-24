package org.pablo.mygas.ui.components

import org.jetbrains.compose.resources.stringResource
import org.pablo.mygas.resources.*
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
import org.pablo.mygas.core.utils.toEuroText
import org.pablo.mygas.core.utils.toLiterPriceText
import org.pablo.mygas.domain.models.GasStation
import org.pablo.mygas.domain.models.FuelType
import org.pablo.mygas.domain.models.priceFor

@Composable
fun GasStationPrices(
    gasStation: GasStation,
    favoriteFuel: FuelType = FuelType.GASOLINE_95,
    tankCapacityLiters: Double? = null,
) {
    Column {
        Text(stringResource(Res.string.prices_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = favoriteFuel.label(),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = gasStation.fuels.priceFor(favoriteFuel).price?.takeIf { it > 0.0 }?.toLiterPriceText() ?: stringResource(Res.string.price_unavailable),
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
                text = stringResource(Res.string.estimated_fill),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = tankCapacityLiters?.let { liters ->
                    gasStation.fuels.priceFor(favoriteFuel).price?.takeIf { it > 0.0 }?.let { (it * liters).toEuroText() } ?: stringResource(Res.string.price_unavailable)
                } ?: stringResource(Res.string.tank_capacity_hint),
                style = MaterialTheme.typography.titleLarge
            )
        }

    }
}

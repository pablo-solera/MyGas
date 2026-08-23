package org.pablo.mygas.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.pablo.mygas.core.utils.GasStationImage
import org.pablo.mygas.domain.models.GasStation
import org.pablo.mygas.domain.models.FuelType
import org.pablo.mygas.domain.models.displayName
import org.pablo.mygas.domain.models.priceFor


@Composable
fun GasStationItem(
    gasStation: GasStation,
    onClick: (GasStation) -> Unit,
    favoriteFuel: FuelType = FuelType.GASOLINE_95,
    tankCapacityLiters: Double? = null,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
    ) {
        Column(
            modifier = Modifier
                .clickable { onClick(gasStation) }
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painterResource(GasStationImage.getImage(gasStation.brand.substringBefore(' '))),
                    contentDescription = gasStation.brand,
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(16.dp))
                        .size(56.dp)
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = gasStation.brand,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                    )
                    Text(
                        text = gasStation.address,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FuelPriceItem(
                    modifier = Modifier.weight(1f),
                    label = favoriteFuel.displayName,
                    price = gasStation.fuels.priceFor(favoriteFuel).price,
                )
                FuelPriceItem(
                    modifier = Modifier.weight(1f),
                    label = "Media",
                    price = gasStation.fuels.priceFor(favoriteFuel).averagePrice
                )
                FuelPriceItem(
                    modifier = Modifier.weight(1f),
                    label = "Depósito",
                    price = tankCapacityLiters?.let { gasStation.fuels.priceFor(favoriteFuel).price?.times(it) },
                    suffix = if (tankCapacityLiters == null) "" else " €"
                )
            }
        }
    }
}

@Composable
private fun FuelPriceItem(
    label: String,
    price: Double?,
    suffix: String = " €/l",
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = price.toPriceText(suffix),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun Double?.toPriceText(suffix: String): String {
    return if (this == null || this <= 0.0) {
        "-- €/l"
    } else {
        this.toString() + suffix
    }
}

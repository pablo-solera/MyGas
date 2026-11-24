package org.pablo.mygas.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NearMe
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.pablo.mygas.core.utils.GasStationImage
import org.pablo.mygas.core.utils.toEuroText
import org.pablo.mygas.core.utils.toLiterPriceText
import org.pablo.mygas.domain.models.FuelType
import org.pablo.mygas.domain.models.GasStation
import org.pablo.mygas.domain.models.UserPreferences
import org.pablo.mygas.domain.models.displayName
import org.pablo.mygas.domain.models.priceFor
import org.pablo.mygas.resources.Res
import org.pablo.mygas.resources.estimated_fill
import org.pablo.mygas.resources.station_go_google_maps
import org.pablo.mygas.resources.station_opening_hours
import org.pablo.mygas.resources.station_other_fuels
import org.pablo.mygas.resources.station_view_detail

/** Quick summary of a station shown when its map pin is tapped. */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun StationQuickSheet(
    station: GasStation,
    preferences: UserPreferences,
    onDismiss: () -> Unit,
    onOpenDetail: () -> Unit,
    onNavigate: () -> Unit,
) {
    val favoriteFuel = preferences.favoriteFuel
    val favoritePrice = station.fuels.priceFor(favoriteFuel)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(GasStationImage.getImage(station.brand.substringBefore(' '))),
                    contentDescription = null,
                    modifier = Modifier.size(56.dp).clip(RoundedCornerShape(16.dp)),
                )
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = station.brand,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = listOf(station.address, station.town).filter { it.isNotBlank() }.joinToString(", "),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(favoriteFuel.displayName, style = MaterialTheme.typography.labelLarge)
                        Text(
                            text = favoritePrice.price.toLiterPriceText(),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    val tankCost = preferences.tankCapacityLiters?.let { liters ->
                        favoritePrice.price?.takeIf { it > 0.0 }?.let { it * liters }
                    }
                    if (tankCost != null) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(stringResource(Res.string.estimated_fill), style = MaterialTheme.typography.labelLarge)
                            Text(
                                text = tankCost.toEuroText(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }

            val otherFuels = FuelType.entries
                .filter { it != favoriteFuel }
                .mapNotNull { fuel ->
                    station.fuels.priceFor(fuel).price?.takeIf { it > 0.0 }?.let { fuel to it }
                }
            if (otherFuels.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(Res.string.station_other_fuels),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        otherFuels.forEach { (fuel, price) ->
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            ) {
                                Text(
                                    text = "${fuel.displayName}  ${price.toLiterPriceText()}",
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                )
                            }
                        }
                    }
                }
            }

            if (station.openingHours.isNotBlank()) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = stringResource(Res.string.station_opening_hours),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(station.openingHours, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(onClick = onOpenDetail, modifier = Modifier.weight(1f)) {
                    Text(stringResource(Res.string.station_view_detail))
                }
                Button(onClick = onNavigate, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Rounded.NearMe, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text(
                        text = stringResource(Res.string.station_go_google_maps),
                        modifier = Modifier.padding(start = 8.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

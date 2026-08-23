package org.pablo.mygas.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.pablo.mygas.domain.models.GasStation
import org.pablo.mygas.domain.models.FuelType

@Composable
fun GasStationList(
    modifier: Modifier = Modifier,
    gasStations: List<GasStation>,
    favoriteFuel: FuelType = FuelType.GASOLINE_95,
    tankCapacityLiters: Double? = null,
    onClick: (GasStation) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            count = gasStations.count(),
            itemContent = { index -> GasStationItem(
                gasStations[index],
                onClick = {onClick(gasStations[index])},
                favoriteFuel = favoriteFuel,
                tankCapacityLiters = tankCapacityLiters,
            ) },
            key = { index -> gasStations[index].id },
            contentType = { "gas-station" }
        )
    }
}

package org.pablo.mygas.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.pablo.mygas.domain.models.GasStation

@Composable
fun GasStationList(
    modifier: Modifier = Modifier,
    gasStations: List<GasStation>,
    onClick: (GasStation) -> Unit
) {
    LazyColumn(
        //columns = GridCells.Adaptive(100.dp),
        // contentPadding = PaddingValues(8.dp),
        // horizontalAlignment = Alignment.CenterHorizontally,
        // horizontalArrangement = Arrangement.spacedBy(4.dp),
        // verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        items(
            count = gasStations.count(),
            itemContent = { index -> GasStationItem(
                gasStations[index],
                onClick = {onClick(gasStations[index])},
            ) },
            key = { index -> gasStations[index].id }
        )
    }
}

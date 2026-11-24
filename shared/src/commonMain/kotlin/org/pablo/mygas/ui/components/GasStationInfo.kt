package org.pablo.mygas.ui.components

import org.jetbrains.compose.resources.stringResource
import org.pablo.mygas.resources.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.pablo.mygas.domain.models.GasStation

@Composable
fun GasStationInfo(
    gasStation: GasStation
) {
    Column {
        Text(gasStation.name, style = MaterialTheme.typography.titleLarge)
        Text(
            gasStation.address,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            stringResource(Res.string.station_schedule),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.semantics { heading() },
        )
        Text(gasStation.openingHours, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(16.dp))
    }
}
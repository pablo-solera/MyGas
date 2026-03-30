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

@Composable
fun GasStationPrices(
    gasStation: GasStation
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
                text = "${gasStation.fuels.diesel.price}€",
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
                text = "${gasStation.fuels.gasoline95.price}€",
                style = MaterialTheme.typography.titleLarge
            )
        }

    }
}
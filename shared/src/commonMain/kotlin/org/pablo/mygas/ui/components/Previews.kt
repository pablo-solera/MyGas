package org.pablo.mygas.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.pablo.mygas.core.utils.NetworkError
import org.pablo.mygas.domain.models.FuelPrice
import org.pablo.mygas.domain.models.FuelPrices
import org.pablo.mygas.domain.models.GasStation
import org.pablo.mygas.domain.models.GeoLocation
import org.pablo.mygas.ui.theme.MyGasTheme

private val previewStation = GasStation(
    id = 1,
    name = "Repsol Gran Vía",
    brand = "REPSOL",
    address = "Calle Gran Vía 12",
    postalCode = "28013",
    town = "Madrid",
    province = "Madrid",
    location = GeoLocation(40.42, -3.70),
    openingHours = "L-D: 24H",
    lastUpdate = "",
    fuels = FuelPrices(
        gasoline95 = FuelPrice(1.459, 1.52),
        gasoline98 = FuelPrice(1.589, 1.64),
        diesel = FuelPrice(1.389, 1.43),
        dieselPremium = FuelPrice.EMPTY,
        biodiesel = FuelPrice.EMPTY,
        glp = FuelPrice.EMPTY,
        hvo = FuelPrice.EMPTY,
    ),
)

@Composable
private fun PreviewSurface(dark: Boolean, content: @Composable () -> Unit) {
    MyGasTheme(darkTheme = dark) {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) { content() }
        }
    }
}

@Preview
@Composable
private fun GasStationItemLightPreview() = PreviewSurface(dark = false) {
    GasStationItem(previewStation, onClick = {}, tankCapacityLiters = 50.0)
}

@Preview
@Composable
private fun GasStationItemDarkPreview() = PreviewSurface(dark = true) {
    GasStationItem(previewStation, onClick = {}, tankCapacityLiters = 50.0)
}

@Preview
@Composable
private fun GasStationPricesPreview() = PreviewSurface(dark = false) {
    GasStationPrices(previewStation, tankCapacityLiters = 40.0)
}

@Preview
@Composable
private fun ErrorStatePreview() = PreviewSurface(dark = false) {
    ErrorState(error = NetworkError.NO_INTERNET, onRetry = {})
}

@Preview
@Composable
private fun EmptyStatePreview() = PreviewSurface(dark = true) {
    NoGasStationsFound()
}

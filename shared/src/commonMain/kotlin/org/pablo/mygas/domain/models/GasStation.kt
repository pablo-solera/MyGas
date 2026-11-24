package org.pablo.mygas.domain.models

data class GasStation(
    val id: Int,
    val name: String,
    val brand: String,
    val address: String,
    val postalCode: String,
    val town: String,
    val province: String,
    val location: GeoLocation,
    val openingHours: String,
    val lastUpdate: String,
    val fuels: FuelPrices,
) {
    val hasValidLocation: Boolean
        get() = location.latitude.isFinite() && location.longitude.isFinite() &&
            (location.latitude != 0.0 || location.longitude != 0.0)
}

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
)
package org.pablo.mygas.domain.models

data class Refuel(
    val id: Int,
    val vehicleId: Int,
    val gasStationId: Int,
    val fuelType: FuelType,
    val price: Double,
    val date: String,
)
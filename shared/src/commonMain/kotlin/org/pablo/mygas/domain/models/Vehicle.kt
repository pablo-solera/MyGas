package org.pablo.mygas.domain.models

data class Vehicle(
    val id: Int,
    val name: String,
    val brand: String,
    val tankCapacity: Int, // Liters
    val fuelType: FuelType,

)
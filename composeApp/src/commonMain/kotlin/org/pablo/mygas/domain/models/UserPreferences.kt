package org.pablo.mygas.domain.models

data class UserPreferences(
    val favoriteFuel: FuelType = FuelType.GASOLINE_95,
    val tankCapacityLiters: Double? = null,
)

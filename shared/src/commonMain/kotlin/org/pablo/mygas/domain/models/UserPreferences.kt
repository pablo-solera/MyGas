package org.pablo.mygas.domain.models

data class UserPreferences(
    val favoriteFuel: FuelType = FuelType.GASOLINE_95,
    val tankCapacityLiters: Double? = null,
) {
    companion object {
        const val MIN_TANK_CAPACITY = 1.0
        const val MAX_TANK_CAPACITY = 200.0
    }
}

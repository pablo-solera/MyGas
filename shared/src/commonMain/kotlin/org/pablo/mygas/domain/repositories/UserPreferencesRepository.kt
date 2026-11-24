package org.pablo.mygas.domain.repositories

import kotlinx.coroutines.flow.StateFlow
import org.pablo.mygas.domain.models.FuelType
import org.pablo.mygas.domain.models.UserPreferences

interface UserPreferencesRepository {
    val preferences: StateFlow<UserPreferences>

    fun setFavoriteFuel(fuelType: FuelType)

    fun setTankCapacityLiters(capacity: Double?)
}

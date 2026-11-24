package org.pablo.mygas.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import org.pablo.mygas.domain.models.FuelType
import org.pablo.mygas.domain.models.UserPreferences
import org.pablo.mygas.domain.repositories.UserPreferencesRepository

class SettingsViewModel(
    private val repository: UserPreferencesRepository,
) : ViewModel() {
    val preferences: StateFlow<UserPreferences> = repository.preferences

    fun setFavoriteFuel(fuelType: FuelType) {
        repository.setFavoriteFuel(fuelType)
    }

    /** An empty input clears the capacity. Returns false when the input is not a valid capacity. */
    fun saveTankCapacity(input: String): Boolean {
        if (input.isBlank()) {
            repository.setTankCapacityLiters(null)
            return true
        }
        val capacity = input.trim().replace(',', '.').toDoubleOrNull()
            ?.takeIf { it in UserPreferences.MIN_TANK_CAPACITY..UserPreferences.MAX_TANK_CAPACITY }
            ?: return false
        repository.setTankCapacityLiters(capacity)
        return true
    }
}

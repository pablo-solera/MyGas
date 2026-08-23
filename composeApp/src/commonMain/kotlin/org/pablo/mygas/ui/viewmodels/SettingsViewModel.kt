package org.pablo.mygas.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.pablo.mygas.domain.models.FuelType
import org.pablo.mygas.domain.models.UserPreferences
import org.pablo.mygas.domain.repositories.UserPreferencesRepository

class SettingsViewModel(
    private val repository: UserPreferencesRepository,
) : ViewModel() {
    val preferences: StateFlow<UserPreferences> = repository.preferences

    fun setFavoriteFuel(fuelType: FuelType) {
        viewModelScope.launch(Dispatchers.IO) { repository.setFavoriteFuel(fuelType) }
    }

    fun saveTankCapacity(input: String): Boolean {
        val capacity = input.trim().replace(',', '.').toDoubleOrNull()
            ?.takeIf { it in 1.0..200.0 }
            ?: return false
        viewModelScope.launch(Dispatchers.IO) { repository.setTankCapacityLiters(capacity) }
        return true
    }

    fun clearTankCapacity() {
        viewModelScope.launch(Dispatchers.IO) { repository.setTankCapacityLiters(null) }
    }
}

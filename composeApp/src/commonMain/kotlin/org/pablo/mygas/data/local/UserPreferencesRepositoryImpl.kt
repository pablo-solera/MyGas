package org.pablo.mygas.data.local

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.pablo.mygas.domain.models.FuelType
import org.pablo.mygas.domain.models.UserPreferences
import org.pablo.mygas.domain.repositories.UserPreferencesRepository

class UserPreferencesRepositoryImpl(
    private val settings: Settings,
) : UserPreferencesRepository {
    private val _preferences = MutableStateFlow(load())
    override val preferences: StateFlow<UserPreferences> = _preferences.asStateFlow()

    override fun setFavoriteFuel(fuelType: FuelType) {
        settings.putString(FAVORITE_FUEL_KEY, fuelType.name)
        _preferences.value = _preferences.value.copy(favoriteFuel = fuelType)
    }

    override fun setTankCapacityLiters(capacity: Double?) {
        if (capacity == null) settings.remove(TANK_CAPACITY_KEY)
        else settings.putDouble(TANK_CAPACITY_KEY, capacity)
        _preferences.value = _preferences.value.copy(tankCapacityLiters = capacity)
    }

    private fun load(): UserPreferences {
        val fuel = settings.getStringOrNull(FAVORITE_FUEL_KEY)
            ?.let { value -> FuelType.entries.firstOrNull { it.name == value } }
            ?: FuelType.GASOLINE_95
        val capacity = settings.getDoubleOrNull(TANK_CAPACITY_KEY)
            ?.takeIf { it in MIN_TANK_CAPACITY..MAX_TANK_CAPACITY }
        return UserPreferences(favoriteFuel = fuel, tankCapacityLiters = capacity)
    }

    private companion object {
        const val FAVORITE_FUEL_KEY = "favorite_fuel"
        const val TANK_CAPACITY_KEY = "tank_capacity_liters"
        const val MIN_TANK_CAPACITY = 1.0
        const val MAX_TANK_CAPACITY = 200.0
    }
}

package org.pablo.mygas.data.local

import com.russhwolf.settings.MapSettings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.pablo.mygas.domain.models.FuelType

class UserPreferencesRepositoryTest {
    @Test
    fun defaultsToGasoline95WithoutTankCapacity() {
        val repository = UserPreferencesRepositoryImpl(MapSettings())

        assertEquals(FuelType.GASOLINE_95, repository.preferences.value.favoriteFuel)
        assertNull(repository.preferences.value.tankCapacityLiters)
    }

    @Test
    fun savesFuelAndCapacity() {
        val settings = MapSettings()
        val repository = UserPreferencesRepositoryImpl(settings)

        repository.setFavoriteFuel(FuelType.GLP)
        repository.setTankCapacityLiters(52.5)

        val reloaded = UserPreferencesRepositoryImpl(settings)
        assertEquals(FuelType.GLP, reloaded.preferences.value.favoriteFuel)
        assertEquals(52.5, reloaded.preferences.value.tankCapacityLiters)
    }

    @Test
    fun ignoresInvalidPersistedCapacity() {
        val settings = MapSettings()
        settings.putDouble("tank_capacity_liters", 500.0)

        val repository = UserPreferencesRepositoryImpl(settings)

        assertNull(repository.preferences.value.tankCapacityLiters)
    }
}

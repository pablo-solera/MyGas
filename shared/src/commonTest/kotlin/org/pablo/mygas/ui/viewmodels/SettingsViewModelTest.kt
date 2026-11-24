package org.pablo.mygas.ui.viewmodels

import com.russhwolf.settings.MapSettings
import org.pablo.mygas.data.local.UserPreferencesRepositoryImpl
import org.pablo.mygas.domain.models.FuelType
import org.pablo.mygas.domain.models.ThemeMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SettingsViewModelTest {
    private fun viewModel() = SettingsViewModel(UserPreferencesRepositoryImpl(MapSettings()))

    @Test
    fun acceptsCommaDecimalWithinRange() {
        val viewModel = viewModel()

        assertTrue(viewModel.saveTankCapacity("52,5"))
        assertEquals(52.5, viewModel.preferences.value.tankCapacityLiters)
    }

    @Test
    fun rejectsValuesOutOfRangeOrNotNumeric() {
        val viewModel = viewModel()

        assertFalse(viewModel.saveTankCapacity("0"))
        assertFalse(viewModel.saveTankCapacity("201"))
        assertFalse(viewModel.saveTankCapacity("abc"))
        assertNull(viewModel.preferences.value.tankCapacityLiters)
    }

    @Test
    fun blankInputClearsTheCapacity() {
        val viewModel = viewModel()
        viewModel.saveTankCapacity("40")

        assertTrue(viewModel.saveTankCapacity("  "))

        assertNull(viewModel.preferences.value.tankCapacityLiters)
    }

    @Test
    fun changingFuelIsPublishedImmediately() {
        val viewModel = viewModel()

        viewModel.setFavoriteFuel(FuelType.DIESEL)

        assertEquals(FuelType.DIESEL, viewModel.preferences.value.favoriteFuel)
    }

    @Test
    fun themeModeIsPublishedAndPersisted() {
        val settings = MapSettings()
        val viewModel = SettingsViewModel(UserPreferencesRepositoryImpl(settings))

        viewModel.setThemeMode(ThemeMode.DARK)

        assertEquals(ThemeMode.DARK, viewModel.preferences.value.themeMode)
        assertEquals(ThemeMode.DARK, UserPreferencesRepositoryImpl(settings).preferences.value.themeMode)
    }
}

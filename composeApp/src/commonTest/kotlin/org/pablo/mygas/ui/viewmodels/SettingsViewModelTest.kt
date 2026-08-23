package org.pablo.mygas.ui.viewmodels

import com.russhwolf.settings.MapSettings
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.pablo.mygas.data.local.UserPreferencesRepositoryImpl

class SettingsViewModelTest {
    @Test
    fun acceptsCommaDecimalWithinRange() {
        val viewModel = SettingsViewModel(UserPreferencesRepositoryImpl(MapSettings()))

        assertTrue(viewModel.saveTankCapacity("52,5"))
        assertFalse(viewModel.saveTankCapacity("0"))
        assertFalse(viewModel.saveTankCapacity("201"))
    }
}

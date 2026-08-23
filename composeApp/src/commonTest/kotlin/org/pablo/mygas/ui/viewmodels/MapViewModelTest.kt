package org.pablo.mygas.ui.viewmodels

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.Test
import kotlin.test.assertEquals
import org.pablo.mygas.core.state.SelectedMunicipalityStore
import org.pablo.mygas.domain.models.FuelPrice
import org.pablo.mygas.domain.models.FuelPrices
import org.pablo.mygas.domain.models.GasStation
import org.pablo.mygas.domain.models.GeoLocation

@kotlinx.coroutines.ExperimentalCoroutinesApi
class MapViewModelTest {
    @Test
    fun `renders markers from the stations already loaded by home`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val store = SelectedMunicipalityStore()
            val viewModel = MapViewModel(store)
            store.updateStations(4354, listOf(station()))

            advanceUntilIdle()

            assertEquals(1, viewModel.uiState.value.markers.size)
            assertEquals("42", viewModel.uiState.value.markers.single().id)
            assertEquals(40.4, viewModel.uiState.value.markers.single().position.latitude)
        } finally {
            Dispatchers.resetMain()
        }
    }

    private fun station() = GasStation(
        id = 42,
        name = "Station",
        brand = "Repsol",
        address = "Address",
        postalCode = "28001",
        town = "Madrid",
        province = "Madrid",
        location = GeoLocation(40.4, -3.7),
        openingHours = "24h",
        lastUpdate = "",
        fuels = FuelPrices(
            gasoline95 = FuelPrice(1.5, 1.6),
            gasoline98 = FuelPrice(null, null),
            diesel = FuelPrice(null, null),
            dieselPremium = FuelPrice(null, null),
            biodiesel = FuelPrice(null, null),
            glp = FuelPrice(null, null),
            hvo = FuelPrice(null, null),
        ),
    )
}

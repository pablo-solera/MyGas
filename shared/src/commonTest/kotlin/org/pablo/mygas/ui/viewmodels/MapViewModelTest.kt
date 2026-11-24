package org.pablo.mygas.ui.viewmodels

import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.pablo.mygas.core.utils.NetworkError
import org.pablo.mygas.core.utils.Result
import org.pablo.mygas.data.local.UserPreferencesRepositoryImpl
import org.pablo.mygas.domain.models.FuelType
import org.pablo.mygas.domain.models.map.MapBounds
import org.pablo.mygas.domain.models.map.MapCoordinate
import org.pablo.mygas.domain.models.map.MapViewport
import org.pablo.mygas.domain.usecases.GetStationsInAreaUseCase
import org.pablo.mygas.testing.FakeStationRepository
import org.pablo.mygas.testing.station
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@kotlinx.coroutines.ExperimentalCoroutinesApi
class MapViewModelTest {

    private fun viewport(lat: Double = 40.4, lon: Double = -3.7, zoom: Float = 13f, span: Double = 0.1) =
        MapViewport(
            center = MapCoordinate(lat, lon),
            zoom = zoom,
            bounds = MapBounds(lat - span / 2, lon - span / 2, lat + span / 2, lon + span / 2),
        )

    private fun viewModel(
        repository: FakeStationRepository,
        preferences: UserPreferencesRepositoryImpl = UserPreferencesRepositoryImpl(MapSettings()),
    ) = MapViewModel(GetStationsInAreaUseCase(repository), preferences)

    private fun mapTest(block: suspend TestScope.() -> Unit) = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            block()
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `first viewport triggers an automatic search and renders markers`() = mapTest {
        val repository = FakeStationRepository(Result.Success(listOf(station(1), station(2, latitude = 40.41))))
        val viewModel = viewModel(repository)

        viewModel.onViewportChanged(viewport())
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(1, repository.requestedBounds.size)
        assertEquals(2, state.map.markers.size)
        assertEquals(2, state.stationCount)
        assertTrue(state.hasSearched)
        assertFalse(state.isSearching)
        assertFalse(state.canSearchHere)
    }

    @Test
    fun `zoomed out viewport asks to zoom in and does not search`() = mapTest {
        val repository = FakeStationRepository()
        val viewModel = viewModel(repository)

        viewModel.onViewportChanged(viewport(zoom = 8f))
        advanceUntilIdle()

        assertTrue(viewModel.state.value.zoomInRequired)
        assertTrue(repository.requestedBounds.isEmpty())
    }

    @Test
    fun `moving the map shows search here and searching again hides it`() = mapTest {
        val repository = FakeStationRepository(Result.Success(listOf(station(1))))
        val viewModel = viewModel(repository)
        viewModel.onViewportChanged(viewport())
        advanceUntilIdle()

        viewModel.onViewportChanged(viewport(lat = 40.4, lon = -3.5))
        assertTrue(viewModel.state.value.canSearchHere)
        assertEquals(1, repository.requestedBounds.size)

        viewModel.searchHere()
        advanceUntilIdle()

        assertEquals(2, repository.requestedBounds.size)
        assertFalse(viewModel.state.value.canSearchHere)
    }

    @Test
    fun `small movements do not offer a new search`() = mapTest {
        val viewModel = viewModel(FakeStationRepository(Result.Success(listOf(station(1)))))
        viewModel.onViewportChanged(viewport())
        advanceUntilIdle()

        viewModel.onViewportChanged(viewport(lon = -3.699))

        assertFalse(viewModel.state.value.canSearchHere)
    }

    @Test
    fun `tapping a marker selects its station and dismissing clears it`() = mapTest {
        val viewModel = viewModel(FakeStationRepository(Result.Success(listOf(station(1), station(2)))))
        viewModel.onViewportChanged(viewport())
        advanceUntilIdle()

        viewModel.onMarkerClick("2")

        assertEquals(2, viewModel.state.value.selectedStation?.id)
        assertTrue(viewModel.state.value.map.markers.single { it.id == "2" }.isSelected)

        viewModel.dismissSelection()

        assertNull(viewModel.state.value.selectedStation)
        assertTrue(viewModel.state.value.map.markers.none { it.isSelected })
    }

    @Test
    fun `search errors are reported and keep the search button available`() = mapTest {
        val viewModel = viewModel(FakeStationRepository(Result.Error(NetworkError.NO_INTERNET)))

        viewModel.onViewportChanged(viewport())
        advanceUntilIdle()

        assertEquals(NetworkError.NO_INTERNET, viewModel.state.value.error)
        assertTrue(viewModel.state.value.canSearchHere)

        viewModel.consumeError()
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `marker price follows the favourite fuel`() = mapTest {
        val repository = FakeStationRepository(Result.Success(listOf(station(1, gasoline95 = 1.5, diesel = 1.4))))
        val preferences = UserPreferencesRepositoryImpl(MapSettings())
        val viewModel = viewModel(repository, preferences)
        viewModel.onViewportChanged(viewport())
        advanceUntilIdle()
        assertEquals(1.5, viewModel.state.value.map.markers.single().fuelPrice?.price)

        preferences.setFavoriteFuel(FuelType.DIESEL)
        advanceUntilIdle()

        assertEquals(1.4, viewModel.state.value.map.markers.single().fuelPrice?.price)
    }

    @Test
    fun `only the closest markers are rendered when there are too many`() {
        val stations = (1..MapViewModel.MAX_MARKERS + 50).map { station(it, latitude = 40.0 + it * 0.001) }

        val markers = MapViewModel.buildMarkers(stations, FuelType.GASOLINE_95, null, viewport(lat = 40.0, lon = -3.7))

        assertEquals(MapViewModel.MAX_MARKERS, markers.size)
        assertEquals("1", markers.first().id)
    }
}

package org.pablo.mygas.ui.viewmodels

import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.pablo.mygas.core.utils.NetworkError
import org.pablo.mygas.core.utils.Result
import org.pablo.mygas.data.local.UserPreferencesRepositoryImpl
import org.pablo.mygas.domain.models.Municipality
import org.pablo.mygas.domain.models.Provincia
import org.pablo.mygas.domain.usecases.GetMunicipalitiesUseCase
import org.pablo.mygas.domain.usecases.GetStationsByMunicipalityUseCase
import org.pablo.mygas.testing.FakeLocationRepository
import org.pablo.mygas.testing.FakeStationRepository
import org.pablo.mygas.testing.station
import org.pablo.mygas.ui.state.Loadable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

@kotlinx.coroutines.ExperimentalCoroutinesApi
class HomeViewModelTest {

    private class Setup(
        val locations: FakeLocationRepository = FakeLocationRepository(),
        val stations: FakeStationRepository = FakeStationRepository(),
    )

    private fun Setup.viewModel(dispatcher: CoroutineDispatcher) = HomeViewModel(
        getMunicipalities = GetMunicipalitiesUseCase(locations),
        getStationsByMunicipality = GetStationsByMunicipalityUseCase(stations),
        preferencesRepository = UserPreferencesRepositoryImpl(MapSettings()),
        cpuDispatcher = dispatcher,
    )

    private fun homeTest(block: suspend kotlinx.coroutines.test.TestScope.() -> Unit) = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            block()
        } finally {
            Dispatchers.resetMain()
        }
    }

    private fun municipality(id: Int, name: String) = Municipality(id, name, "28")

    @Test
    fun `latest province response wins and previous request is cancelled`() = homeTest {
        val setup = Setup()
        val barcelona = municipality(8, "Barcelona")
        setup.locations.municipalities[Provincia.MADRID.id] = CompletableDeferred()
        setup.locations.municipalities[Provincia.BARCELONA.id] = CompletableDeferred(Result.Success(listOf(barcelona)))
        val viewModel = setup.viewModel(StandardTestDispatcher(testScheduler))

        runCurrent()
        viewModel.selectProvincia(Provincia.BARCELONA)
        advanceUntilIdle()

        assertEquals(Provincia.BARCELONA, viewModel.uiState.value.provincia)
        assertEquals(listOf(barcelona), viewModel.uiState.value.municipios)
        assertTrue(Provincia.MADRID.id in setup.locations.cancelled)
    }

    @Test
    fun `selecting the loaded province does not issue another request`() = homeTest {
        val setup = Setup()
        setup.locations.municipalities[Provincia.MADRID.id] =
            CompletableDeferred(Result.Success(listOf(municipality(28, "Madrid"))))
        val viewModel = setup.viewModel(StandardTestDispatcher(testScheduler))

        advanceUntilIdle()
        viewModel.selectProvincia(Provincia.MADRID)
        advanceUntilIdle()

        assertEquals(1, setup.locations.calls.count { it == Provincia.MADRID.id })
    }

    @Test
    fun `municipalities are sorted by name`() = homeTest {
        val setup = Setup()
        setup.locations.municipalities[Provincia.MADRID.id] = CompletableDeferred(
            Result.Success(listOf(municipality(2, "Zarzalejo"), municipality(1, "Alcalá")))
        )
        val viewModel = setup.viewModel(StandardTestDispatcher(testScheduler))

        advanceUntilIdle()

        assertEquals(listOf("Alcalá", "Zarzalejo"), viewModel.uiState.value.municipios?.map { it.name })
    }

    @Test
    fun `retry after a failed first load requests the same municipality again`() = homeTest {
        val setup = Setup()
        setup.stations.municipalityResults[7] = CompletableDeferred(Result.Error(NetworkError.NO_INTERNET))
        val viewModel = setup.viewModel(StandardTestDispatcher(testScheduler))
        advanceUntilIdle()

        viewModel.getGasolinerasByMunicipio(7)
        advanceUntilIdle()
        assertIs<Loadable.Failed>(viewModel.uiState.value.gasStationsState)

        setup.stations.municipalityResults[7] = CompletableDeferred(Result.Success(listOf(station(1))))
        viewModel.retryGasolineras()
        advanceUntilIdle()

        assertIs<Loadable.Content<*>>(viewModel.uiState.value.gasStationsState)
        assertEquals(1, viewModel.uiState.value.gasStations?.size)
    }

    @Test
    fun `stations without price are sorted last`() = homeTest {
        val setup = Setup()
        setup.stations.municipalityResults[7] = CompletableDeferred(
            Result.Success(listOf(station(1, gasoline95 = null), station(2, gasoline95 = 1.7), station(3, gasoline95 = 1.5)))
        )
        val viewModel = setup.viewModel(StandardTestDispatcher(testScheduler))
        advanceUntilIdle()

        viewModel.getGasolinerasByMunicipio(7)
        advanceUntilIdle()

        assertEquals(listOf(3, 2, 1), viewModel.uiState.value.gasStations.orEmpty().map { it.id })
    }

    @Test
    fun `changing municipality clears the previous stations while loading`() = homeTest {
        val setup = Setup()
        setup.stations.municipalityResults[7] = CompletableDeferred(Result.Success(listOf(station(1))))
        setup.stations.municipalityResults[8] = CompletableDeferred()
        val viewModel = setup.viewModel(StandardTestDispatcher(testScheduler))
        advanceUntilIdle()
        viewModel.getGasolinerasByMunicipio(7)
        advanceUntilIdle()

        viewModel.getGasolinerasByMunicipio(8)
        runCurrent()

        assertNull(viewModel.uiState.value.gasStations)
        assertEquals(Loadable.Loading, viewModel.uiState.value.gasStationsState)
        assertEquals(8, viewModel.uiState.value.municipioId)
    }
}

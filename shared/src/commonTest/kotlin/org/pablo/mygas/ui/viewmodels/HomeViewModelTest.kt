package org.pablo.mygas.ui.viewmodels

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
import org.pablo.mygas.core.utils.Provincia
import org.pablo.mygas.core.utils.Result
import org.pablo.mygas.data.remote.GovernmentGasStationResponse
import org.pablo.mygas.data.remote.MunicipioItem
import org.pablo.mygas.data.remote.responses.GasolinerasResponse
import org.pablo.mygas.domain.usecases.GetAllGasolinerasByMunicipioUseCase
import org.pablo.mygas.domain.usecases.GetAllMunicipiosByProvinciaUseCase
import org.pablo.mygas.testing.FakeGovernmentSource
import org.pablo.mygas.ui.state.Loadable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

@kotlinx.coroutines.ExperimentalCoroutinesApi
class HomeViewModelTest {
    @Test
    fun `latest province response wins and previous request is cancelled`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val source = FakeGovernmentSource()
            val barcelona = municipio("08", "Barcelona")
            source.municipios[Provincia.MADRID.id] = CompletableDeferred()
            source.municipios[Provincia.BARCELONA.id] = CompletableDeferred(Result.Success(listOf(barcelona)))
            val viewModel = source.viewModel(StandardTestDispatcher(testScheduler))

            runCurrent()
            viewModel.selectProvincia(Provincia.BARCELONA)
            advanceUntilIdle()

            assertEquals(Provincia.BARCELONA, viewModel.uiState.value.provincia)
            assertEquals(listOf(barcelona), viewModel.uiState.value.municipios)
            assertTrue(Provincia.MADRID.id in source.cancelledMunicipios)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `selecting the loaded province does not issue another request`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val source = FakeGovernmentSource()
            source.municipios[Provincia.MADRID.id] = CompletableDeferred(
                Result.Success(listOf(municipio(28, "Madrid")))
            )
            val viewModel = source.viewModel(StandardTestDispatcher(testScheduler))

            advanceUntilIdle()
            viewModel.selectProvincia(Provincia.MADRID)
            advanceUntilIdle()

            assertEquals(1, source.municipalityCalls.count { it == Provincia.MADRID.id })
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `retry after a failed first load requests the same municipality again`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val source = FakeGovernmentSource()
            source.gasolineras[7] = CompletableDeferred(Result.Error(NetworkError.NO_INTERNET))
            val viewModel = source.viewModel(StandardTestDispatcher(testScheduler))
            advanceUntilIdle()

            viewModel.getGasolinerasByMunicipio(7)
            advanceUntilIdle()
            assertIs<Loadable.Failed>(viewModel.uiState.value.gasStationsState)

            source.gasolineras[7] = CompletableDeferred(Result.Success(GasolinerasResponse(stations = listOf(govStation("1", "1,500")))))
            viewModel.retryGasolineras()
            advanceUntilIdle()

            assertIs<Loadable.Content<*>>(viewModel.uiState.value.gasStationsState)
            assertEquals(1, viewModel.uiState.value.gasStations?.size)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `stations without price are sorted last and averages are filled`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val source = FakeGovernmentSource()
            source.gasolineras[7] = CompletableDeferred(
                Result.Success(
                    GasolinerasResponse(
                        stations = listOf(
                            govStation("1", null),
                            govStation("2", "1,700"),
                            govStation("3", "1,500"),
                        )
                    )
                )
            )
            val viewModel = source.viewModel(StandardTestDispatcher(testScheduler))
            advanceUntilIdle()

            viewModel.getGasolinerasByMunicipio(7)
            advanceUntilIdle()

            val stations = viewModel.uiState.value.gasStations.orEmpty()
            assertEquals(listOf(3, 2, 1), stations.map { it.id })
            assertEquals(1.6, stations.first().fuels.gasoline95.averagePrice ?: 0.0, 1e-9)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `changing municipality clears the previous stations while loading`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val source = FakeGovernmentSource()
            source.gasolineras[7] = CompletableDeferred(Result.Success(GasolinerasResponse(stations = listOf(govStation("1", "1,5")))))
            source.gasolineras[8] = CompletableDeferred()
            val viewModel = source.viewModel(StandardTestDispatcher(testScheduler))
            advanceUntilIdle()
            viewModel.getGasolinerasByMunicipio(7)
            advanceUntilIdle()

            viewModel.getGasolinerasByMunicipio(8)
            runCurrent()

            assertNull(viewModel.uiState.value.gasStations)
            assertEquals(Loadable.Loading, viewModel.uiState.value.gasStationsState)
            assertEquals(8, viewModel.uiState.value.municipioId)
        } finally {
            Dispatchers.resetMain()
        }
    }

    private fun FakeGovernmentSource.viewModel(dispatcher: CoroutineDispatcher) = HomeViewModel(
        getAllGasolinerasByMunicipioUseCase = GetAllGasolinerasByMunicipioUseCase(this),
        getAllMunicipiosByProvinciaUseCase = GetAllMunicipiosByProvinciaUseCase(this),
        cpuDispatcher = dispatcher,
    )

    private fun govStation(id: String, price: String?) = GovernmentGasStationResponse(
        stationId = id,
        label = "Repsol",
        latitude = "40,4",
        longitude = "-3,7",
        priceGasoline95E5 = price,
    )

    private fun municipio(id: Any, name: String) = MunicipioItem(
        ccaa = "Comunidad",
        idCcaa = "1",
        idMunicipio = id.toString(),
        idProvincia = "28",
        municipio = name,
        provincia = "Provincia"
    )
}

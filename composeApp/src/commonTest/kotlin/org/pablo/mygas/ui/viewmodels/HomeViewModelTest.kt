package org.pablo.mygas.ui.viewmodels

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.pablo.mygas.core.utils.NetworkError
import org.pablo.mygas.core.utils.Provincia
import org.pablo.mygas.core.utils.Result
import org.pablo.mygas.core.state.SelectedMunicipalityStore
import org.pablo.mygas.data.remote.GovernmentGasStationResponse
import org.pablo.mygas.data.remote.MunicipioItem
import org.pablo.mygas.data.remote.datasources.GovernmentGasStationDataSource
import org.pablo.mygas.data.remote.responses.ComunidadAutonomaResponse
import org.pablo.mygas.data.remote.responses.GasolinerasResponse
import org.pablo.mygas.data.remote.responses.PrecioilGasStationResponse
import org.pablo.mygas.domain.usecases.GetAllGasolinerasByMunicipioUseCase
import org.pablo.mygas.domain.usecases.GetAllMunicipiosByProvinciaUseCase
import org.pablo.mygas.domain.usecases.GetGasolineraByIdUseCase

@kotlinx.coroutines.ExperimentalCoroutinesApi
class HomeViewModelTest {
    @Test
    fun `latest province response wins and previous request is cancelled`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val source = FakeGovernmentSource()
            val madridResponse = CompletableDeferred<Result<List<MunicipioItem>, NetworkError>>()
            val barcelona = municipio("08", "Barcelona")
            source.municipios[Provincia.MADRID.id] = madridResponse
            source.municipios[Provincia.BARCELONA.id] = CompletableDeferred(
                Result.Success(listOf(barcelona))
            )
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

    private fun FakeGovernmentSource.viewModel(dispatcher: kotlinx.coroutines.CoroutineDispatcher) = HomeViewModel(
        getAllGasolinerasByMunicipioUseCase = GetAllGasolinerasByMunicipioUseCase(this),
        getAllMunicipiosByProvinciaUseCase = GetAllMunicipiosByProvinciaUseCase(this),
        getGasolineraByIdUseCase = GetGasolineraByIdUseCase(this),
        cpuDispatcher = dispatcher,
        selectionStore = SelectedMunicipalityStore(),
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

private class FakeGovernmentSource : GovernmentGasStationDataSource {
    val municipios = mutableMapOf<String, CompletableDeferred<Result<List<MunicipioItem>, NetworkError>>>()
    val municipalityCalls = mutableListOf<String>()
    val cancelledMunicipios = mutableSetOf<String>()

    override suspend fun getGasolinerasByMunicipio(
        municipioId: Int
    ): Result<GasolinerasResponse, NetworkError> = Result.Success(
        GasolinerasResponse(
            Fecha = "",
            ListaEESSPrecio = emptyList(),
            Nota = "",
            ResultadoConsulta = ""
        )
    )

    override suspend fun getComunidadesAutonomas(): Result<List<ComunidadAutonomaResponse>, NetworkError> =
        Result.Error(NetworkError.UNKNOWN)

    override suspend fun getMunicipiosByProvincia(
        provinciaId: String
    ): Result<List<MunicipioItem>, NetworkError> {
        municipalityCalls += provinciaId
        return try {
            municipios.getValue(provinciaId).await()
        } catch (e: CancellationException) {
            cancelledMunicipios += provinciaId
            throw e
        }
    }

    override suspend fun getGasolineraById(
        id: Int
    ): Result<PrecioilGasStationResponse, NetworkError> = Result.Error(NetworkError.UNKNOWN)
}

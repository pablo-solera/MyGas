package org.pablo.mygas.data.remote.repository

import org.pablo.mygas.data.remote.MunicipioItem
import org.pablo.mygas.data.remote.MunicipioService
import org.pablo.mygas.data.remote.datasources.GovernmentGasStationDataSource
import org.pablo.mygas.data.remote.responses.ComunidadAutonomaResponse
import org.pablo.mygas.data.remote.responses.PrecioilGasStationResponse
import org.pablo.mygas.data.remote.responses.GasolinerasResponse
import org.pablo.mygas.core.utils.NetworkError
import org.pablo.mygas.core.utils.Result

class GasolinerasRepository(
    private val municipioService: MunicipioService
) : GovernmentGasStationDataSource {

    override suspend fun getGasolinerasByMunicipio(municipioId: Int): Result<GasolinerasResponse, NetworkError> {
        return municipioService.getGasolinerasByMunicipio(municipioId)
    }


    override suspend fun getComunidadesAutonomas(): Result<List<ComunidadAutonomaResponse>, NetworkError> {
        return municipioService.getComunidadesAutonomas()
    }

    override suspend fun getMunicipiosByProvincia(provinciaId: String): Result<List<MunicipioItem>, NetworkError> {
        return municipioService.getMunicipiosByProvincia(provinciaId)
    }

    override suspend fun getGasolineraById(id: Int): Result<PrecioilGasStationResponse, NetworkError> {
    return municipioService.getGasolineraById(id)
    }
}

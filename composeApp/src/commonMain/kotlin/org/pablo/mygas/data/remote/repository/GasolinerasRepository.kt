package org.pablo.mygas.data.remote.repository

import org.pablo.mygas.data.remote.MunicipioItem
import org.pablo.mygas.data.remote.MunicipioService
import org.pablo.mygas.data.remote.responses.ComunidadAutonomaResponse
import org.pablo.mygas.data.remote.responses.GasStationResponse
import org.pablo.mygas.data.remote.responses.GasolinerasResponse
import org.pablo.mygas.core.utils.NetworkError
import org.pablo.mygas.core.utils.Result

class GasolinerasRepository(
    private val municipioService: MunicipioService
) {

    suspend fun getGasolinerasByMunicipio(municipioId: Int): Result<GasolinerasResponse, NetworkError> {
        return municipioService.getGasolinerasByMunicipio(municipioId)
    }


    suspend fun getComunidadesAutonomas(): Result<List<ComunidadAutonomaResponse>, NetworkError> {
        return municipioService.getComunidadesAutonomas()
    }

    suspend fun getMunicipiosByProvincia(provinciaId: Int): Result<List<MunicipioItem>, NetworkError> {
        return municipioService.getMunicipiosByProvincia(provinciaId)
    }

    suspend fun getGasolineraById(id: Int): Result<GasStationResponse, NetworkError> {
    return municipioService.getGasolineraById(id)
    }
}
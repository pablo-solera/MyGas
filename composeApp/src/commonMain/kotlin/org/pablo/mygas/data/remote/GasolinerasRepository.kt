package org.pablo.mygas.data.remote

import org.pablo.mygas.data.remote.responses.ComunidadAutonomaResponse
import org.pablo.mygas.data.remote.responses.GasolinerasResponse
import org.pablo.mygas.utils.NetworkError
import org.pablo.mygas.utils.Result

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
}
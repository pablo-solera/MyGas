package org.pablo.mygas.data

import io.ktor.client.call.body
import org.pablo.mygas.utils.NetworkError
import org.pablo.mygas.utils.Result

class GasolinerasRepository(
    private val municipioService: MunicipioService
) {
    suspend fun getGasolinerasByMunicipio(municipioId: String): List<ListaEESSPrecio> {
        return municipioService.getEstacionesTerrestresByMunicipio(municipioId)
            .body<GasolinerasResponse>().ListaEESSPrecio
    }

    suspend fun getMunicipiosByProvincia(provinciaId: String): List<MunicipioItem> {
        return municipioService.getMunicipiosByProvincia(provinciaId).body()
    }

    suspend fun getComunidadesAutonomas(): List<ComunidadAutonoma> {
        return municipioService.getComunidadesAutonomas().body()
    }

    suspend fun getComunidadesAutonomas2(): Result<List<ComunidadAutonoma>, NetworkError> {
        return municipioService.getComunidadesAutonomas2()
    }

    suspend fun getMunicipiosByProvincia2(provinciaId: Int): Result<List<MunicipioItem>, NetworkError> {
        return municipioService.getMunicipiosByProvincia2(provinciaId)
    }
}
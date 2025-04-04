package org.pablo.mygas.data

import io.ktor.client.call.body

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
}
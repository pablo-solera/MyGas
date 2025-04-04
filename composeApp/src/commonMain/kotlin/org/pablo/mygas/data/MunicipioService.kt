package org.pablo.mygas.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.request
import io.ktor.util.reflect.TypeInfo

class MunicipioService(
    private val client: HttpClient
) {

    suspend fun getMunicipiosByProvincia(provinciaId: String): HttpResponse {
        return client
            .get("https://sedeaplicaciones.minetur.gob.es/ServiciosRESTCarburantes/PreciosCarburantes/Listados/MunicipiosPorProvincia/$provinciaId")
    }

    suspend fun getEstacionesTerrestresByMunicipio(municipioId: String): HttpResponse {
        return client
            .get("https://sedeaplicaciones.minetur.gob.es/ServiciosRESTCarburantes/PreciosCarburantes/EstacionesTerrestres/FiltroMunicipio/$municipioId")
    }

    suspend fun getComunidadesAutonomas(): HttpResponse {
        return client
            .get("https://sedeaplicaciones.minetur.gob.es/ServiciosRESTCarburantes/PreciosCarburantes/Listados/ComunidadesAutonomas")
    }

    suspend fun getGasolineraById(gasolineraId: String): HttpResponse {
        return client
            .get("https://sedeaplicaciones.minetur.gob.es/ServiciosRESTCarburantes/PreciosCarburantes/EstacionesTerrestres/Detalle/${gasolineraId}")
    }
}
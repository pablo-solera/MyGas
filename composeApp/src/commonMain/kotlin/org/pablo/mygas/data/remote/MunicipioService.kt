package org.pablo.mygas.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.serialization.SerializationException
import org.pablo.mygas.data.remote.responses.ComunidadAutonomaResponse
import org.pablo.mygas.data.remote.responses.GasStationResponse
import org.pablo.mygas.data.remote.responses.GasolinerasResponse
import org.pablo.mygas.core.utils.NetworkError
import org.pablo.mygas.core.utils.Result


class MunicipioService(
    private val client: HttpClient
) {

    suspend fun getComunidadesAutonomas(): Result<List<ComunidadAutonomaResponse>, NetworkError> {
        val response = try {
            client.get("https://sedeaplicaciones.minetur.gob.es/ServiciosRESTCarburantes/PreciosCarburantes/Listados/ComunidadesAutonomas")
        } catch (e: UnresolvedAddressException) {
            return Result.Error(NetworkError.NO_INTERNET)
        } catch (e: SerializationException) {
            return Result.Error(NetworkError.SERIALIZATION)
        } catch (e: Exception) {
            return Result.Error(NetworkError.UNKNOWN)
        }
        return when (response.status.value) {
            in 200..299 -> {
                val responseBody = response.body<List<ComunidadAutonomaResponse>>()
                Result.Success(responseBody)
            }

            401 -> Result.Error(NetworkError.UNAUTHORIZED)
            403 -> Result.Error(NetworkError.FORBIDDEN)
            404 -> Result.Error(NetworkError.NOT_FOUND)
            408 -> Result.Error(NetworkError.REQUEST_TIMEOUT)
            409 -> Result.Error(NetworkError.CONFLICT)
            429 -> Result.Error(NetworkError.TOO_MANY_REQUESTS)
            413 -> Result.Error(NetworkError.PAYLOAD_TOO_LARGE)
            in 500..599 -> Result.Error(NetworkError.SERVER_ERROR)
            else -> Result.Error(NetworkError.UNKNOWN)
        }
    }


    suspend fun getMunicipiosByProvincia(provinciaId: Int): Result<List<MunicipioItem>, NetworkError> {

        val response = try {
            client.get("https://sedeaplicaciones.minetur.gob.es/ServiciosRESTCarburantes/PreciosCarburantes/Listados/MunicipiosPorProvincia/$provinciaId")
        } catch (e: UnresolvedAddressException) {
            return Result.Error(NetworkError.NO_INTERNET)
        } catch (e: SerializationException) {
            return Result.Error(NetworkError.SERIALIZATION)
        } catch (e: Exception) {
            return Result.Error(NetworkError.UNKNOWN)
        }
        return when (response.status.value) {
            in 200..299 -> {
                val responseBody = response.body<List<MunicipioItem>>()
                Result.Success(responseBody)
            }

            408 -> Result.Error(NetworkError.REQUEST_TIMEOUT)
            in 500..599 -> Result.Error(NetworkError.SERVER_ERROR)
            else -> Result.Error(NetworkError.UNKNOWN)
        }
    }


    suspend fun getGasolinerasByMunicipio(municipioId: Int): Result<GasolinerasResponse, NetworkError> {
        val response = try {
            client.get("https://sedeaplicaciones.minetur.gob.es/ServiciosRESTCarburantes/PreciosCarburantes/EstacionesTerrestres/FiltroMunicipio/$municipioId")
        } catch (e: UnresolvedAddressException) {
            return Result.Error(NetworkError.NO_INTERNET)
        } catch (e: SerializationException) {
            return Result.Error(NetworkError.SERIALIZATION)
        } catch (e: Exception) {
            return Result.Error(NetworkError.UNKNOWN)
        }
        return when (response.status.value) {
            in 200..299 -> {
                val responseBody = response.body<GasolinerasResponse>()
                Result.Success(responseBody)
            }

            408 -> Result.Error(NetworkError.REQUEST_TIMEOUT)
            in 500..599 -> Result.Error(NetworkError.SERVER_ERROR)
            else -> Result.Error(NetworkError.UNKNOWN)
        }
    }

    suspend fun getGasolineraById(id: Int): Result<GasStationResponse, NetworkError> {
     val response = try {
            client.get("https://api.precioil.es/estaciones/detalles/$id")
        } catch (e: UnresolvedAddressException) {
            return Result.Error(NetworkError.NO_INTERNET)
        } catch (e: SerializationException) {
            return Result.Error(NetworkError.SERIALIZATION)
        } catch (e: Exception) {
            return Result.Error(NetworkError.UNKNOWN)
        }
        return when (response.status.value) {
            in 200..299 -> {
                val responseBody = response.body<GasStationResponse>()
                Result.Success(responseBody)
            }

            408 -> Result.Error(NetworkError.REQUEST_TIMEOUT)
            in 500..599 -> Result.Error(NetworkError.SERVER_ERROR)
            else -> Result.Error(NetworkError.UNKNOWN)
        }
    }

}
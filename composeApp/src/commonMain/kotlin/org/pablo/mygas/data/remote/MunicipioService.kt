package org.pablo.mygas.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.get
import io.ktor.serialization.JsonConvertException
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import org.pablo.mygas.data.remote.responses.ComunidadAutonomaResponse
import org.pablo.mygas.data.remote.responses.PrecioilGasStationResponse
import org.pablo.mygas.data.remote.responses.GasolinerasResponse
import org.pablo.mygas.core.utils.NetworkError
import org.pablo.mygas.core.utils.Result


class MunicipioService(
    private val client: HttpClient
) {

    suspend fun getComunidadesAutonomas(): Result<List<ComunidadAutonomaResponse>, NetworkError> {
        return get("https://sedeaplicaciones.minetur.gob.es/ServiciosRESTCarburantes/PreciosCarburantes/Listados/ComunidadesAutonomas")
    }


    suspend fun getMunicipiosByProvincia(provinciaId: String): Result<List<MunicipioItem>, NetworkError> {

        return get("https://sedeaplicaciones.minetur.gob.es/ServiciosRESTCarburantes/PreciosCarburantes/Listados/MunicipiosPorProvincia/$provinciaId")
    }


    suspend fun getGasolinerasByMunicipio(municipioId: Int): Result<GasolinerasResponse, NetworkError> {
        return get("https://sedeaplicaciones.minetur.gob.es/ServiciosRESTCarburantes/PreciosCarburantes/EstacionesTerrestres/FiltroMunicipio/$municipioId")
    }

    suspend fun getGasolineraById(id: Int): Result<PrecioilGasStationResponse, NetworkError> {
        return get("https://api.precioil.es/estaciones/detalles/$id")
    }

    private suspend inline fun <reified T> get(url: String): Result<T, NetworkError> {
        return try {
            val response = client.get(url)
            when (response.status.value) {
                in 200..299 -> Result.Success(response.body<T>())
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
        } catch (e: CancellationException) {
            throw e
        } catch (e: UnresolvedAddressException) {
            Result.Error(NetworkError.NO_INTERNET)
        } catch (e: HttpRequestTimeoutException) {
            Result.Error(NetworkError.REQUEST_TIMEOUT)
        } catch (e: SerializationException) {
            Result.Error(NetworkError.SERIALIZATION)
        } catch (e: JsonConvertException) {
            Result.Error(NetworkError.SERIALIZATION)
        } catch (e: Exception) {
            Result.Error(NetworkError.UNKNOWN)
        }
    }

}

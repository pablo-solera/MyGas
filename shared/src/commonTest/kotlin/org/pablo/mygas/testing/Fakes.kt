package org.pablo.mygas.testing

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.Json
import org.pablo.mygas.core.utils.NetworkError
import org.pablo.mygas.core.utils.Result
import org.pablo.mygas.data.local.database.GasStationLocalDataSource
import org.pablo.mygas.data.remote.MunicipioItem
import org.pablo.mygas.data.remote.datasources.GovernmentGasStationDataSource
import org.pablo.mygas.data.remote.datasources.PrecioilGasStationDataSource
import org.pablo.mygas.data.remote.responses.ComunidadAutonomaResponse
import org.pablo.mygas.data.remote.responses.GasolinerasResponse
import org.pablo.mygas.data.remote.responses.PrecioilGasStationResponse
import org.pablo.mygas.domain.models.FuelPrice
import org.pablo.mygas.domain.models.FuelPrices
import org.pablo.mygas.domain.models.GasStation
import org.pablo.mygas.domain.models.GeoLocation
import org.pablo.mygas.domain.models.map.MapBounds
import org.pablo.mygas.domain.repositories.GasStationRepository

fun testClient(handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData): HttpClient =
    HttpClient(MockEngine) {
        engine { addHandler(handler) }
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true; coerceInputValues = true }) }
        install(HttpTimeout)
    }

fun MockRequestHandleScope.jsonResponse(body: String, status: HttpStatusCode = HttpStatusCode.OK) = respond(
    content = body,
    status = status,
    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
)

fun station(
    id: Int = 1,
    latitude: Double = 40.4,
    longitude: Double = -3.7,
    gasoline95: Double? = 1.5,
    diesel: Double? = null,
    brand: String = "Repsol",
) = GasStation(
    id = id,
    name = "Station $id",
    brand = brand,
    address = "Address $id",
    postalCode = "28001",
    town = "Madrid",
    province = "Madrid",
    location = GeoLocation(latitude, longitude),
    openingHours = "24h",
    lastUpdate = "",
    fuels = FuelPrices(
        gasoline95 = FuelPrice(gasoline95, null),
        gasoline98 = FuelPrice.EMPTY,
        diesel = FuelPrice(diesel, null),
        dieselPremium = FuelPrice.EMPTY,
        biodiesel = FuelPrice.EMPTY,
        glp = FuelPrice.EMPTY,
        hvo = FuelPrice.EMPTY,
    ),
)

class FakeGovernmentSource : GovernmentGasStationDataSource {
    val municipios = mutableMapOf<String, CompletableDeferred<Result<List<MunicipioItem>, NetworkError>>>()
    val gasolineras = mutableMapOf<Int, CompletableDeferred<Result<GasolinerasResponse, NetworkError>>>()
    val municipalityCalls = mutableListOf<String>()
    val cancelledMunicipios = mutableSetOf<String>()

    override suspend fun getGasolinerasByMunicipio(municipioId: Int): Result<GasolinerasResponse, NetworkError> =
        gasolineras[municipioId]?.await() ?: Result.Success(GasolinerasResponse())

    override suspend fun getComunidadesAutonomas(): Result<List<ComunidadAutonomaResponse>, NetworkError> =
        Result.Error(NetworkError.UNKNOWN)

    override suspend fun getMunicipiosByProvincia(provinciaId: String): Result<List<MunicipioItem>, NetworkError> {
        municipalityCalls += provinciaId
        return try {
            municipios[provinciaId]?.await() ?: Result.Success(emptyList())
        } catch (e: kotlinx.coroutines.CancellationException) {
            cancelledMunicipios += provinciaId
            throw e
        }
    }

    override suspend fun getGasolineraById(id: Int): Result<PrecioilGasStationResponse, NetworkError> =
        Result.Error(NetworkError.UNKNOWN)
}

class FakePrecioilSource : PrecioilGasStationDataSource {
    override suspend fun getById(gasStationId: Int): PrecioilGasStationResponse =
        throw org.pablo.mygas.core.utils.NetworkException(NetworkError.UNKNOWN)
}

class FakeFavorites : GasStationLocalDataSource {
    private val ids = MutableStateFlow<List<Int>>(emptyList())
    override fun observeFavoriteIds(): Flow<List<Int>> = ids
    override suspend fun getAllFavoriteIds(): List<Int> = ids.value
    override suspend fun addFavorite(id: Int) { ids.value = ids.value + id }
    override suspend fun removeFavorite(id: Int) { ids.value = ids.value - id }
}

class FakeStationRepository(
    var result: Result<List<GasStation>, NetworkError> = Result.Success(emptyList()),
) : GasStationRepository {
    val requestedBounds = mutableListOf<MapBounds>()

    override suspend fun getStationsInArea(bounds: MapBounds): Result<List<GasStation>, NetworkError> {
        requestedBounds += bounds
        return result
    }
}

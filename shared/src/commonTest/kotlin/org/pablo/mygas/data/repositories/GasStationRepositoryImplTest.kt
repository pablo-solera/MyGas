package org.pablo.mygas.data.repositories

import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.pablo.mygas.core.utils.NetworkError
import org.pablo.mygas.core.utils.Result
import org.pablo.mygas.data.remote.MunicipioService
import org.pablo.mygas.domain.models.map.MapBounds
import org.pablo.mygas.testing.jsonResponse
import org.pablo.mygas.testing.testClient
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TestTimeSource

class GasStationRepositoryImplTest {
    private val payload = """
        {"Fecha":"","ResultadoConsulta":"OK","ListaEESSPrecio":[
          {"IDEESS":"1","Rótulo":"REPSOL","Latitud":"40,4","Longitud (WGS84)":"-3,7","Precio Gasolina 95 E5":"1,400"},
          {"IDEESS":"2","Rótulo":"CEPSA","Latitud":"40,5","Longitud (WGS84)":"-3,6","Precio Gasolina 95 E5":"1,600"},
          {"IDEESS":"3","Rótulo":"FAR","Latitud":"41,9","Longitud (WGS84)":"-3,6","Precio Gasolina 95 E5":"1,500"},
          {"IDEESS":"4","Rótulo":"NOCOORDS","Latitud":"","Longitud (WGS84)":""},
          {"IDEESS":"x","Rótulo":"BADID","Latitud":"40,4","Longitud (WGS84)":"-3,7"}
        ]}
    """.trimIndent()

    private val madrid = MapBounds(south = 40.0, west = -4.0, north = 41.0, east = -3.0)

    @Test
    fun `returns only valid stations inside the area with averages over that area`() = runTest {
        val client = testClient { jsonResponse(payload) }
        val repository = GasStationRepositoryImpl(MunicipioService(client), TestTimeSource(), cpuDispatcher = StandardTestDispatcher(testScheduler))

        val result = repository.getStationsInArea(madrid)

        assertIs<Result.Success<*>>(result)
        val stations = (result as Result.Success).data
        assertEquals(listOf(1, 2), stations.map { it.id })
        assertEquals(1.5, stations.first().fuels.gasoline95.averagePrice ?: 0.0, 1e-9)
        client.close()
    }

    @Test
    fun `the national list is downloaded once and reused for different areas`() = runTest {
        var requests = 0
        val client = testClient { requests++; jsonResponse(payload) }
        val repository = GasStationRepositoryImpl(MunicipioService(client), TestTimeSource(), cpuDispatcher = StandardTestDispatcher(testScheduler))

        repository.getStationsInArea(madrid)
        val north = repository.getStationsInArea(MapBounds(41.5, -4.0, 42.5, -3.0))

        assertEquals(1, requests)
        assertEquals(listOf(3), (north as Result.Success).data.map { it.id })
        client.close()
    }

    @Test
    fun `a stale list is served when the refresh fails`() = runTest {
        var requests = 0
        val time = TestTimeSource()
        val client = testClient {
            requests++
            if (requests == 1) jsonResponse(payload) else jsonResponse("", HttpStatusCode.InternalServerError)
        }
        val repository = GasStationRepositoryImpl(MunicipioService(client), time, cacheTtl = 30.minutes, cpuDispatcher = StandardTestDispatcher(testScheduler))
        repository.getStationsInArea(madrid)
        time += 31.minutes

        val result = repository.getStationsInArea(madrid)

        assertEquals(2, requests)
        assertEquals(2, (result as Result.Success).data.size)
        client.close()
    }

    @Test
    fun `an error is returned when there is nothing cached`() = runTest {
        val client = testClient { jsonResponse("", HttpStatusCode.ServiceUnavailable) }
        val repository = GasStationRepositoryImpl(MunicipioService(client), TestTimeSource(), cpuDispatcher = StandardTestDispatcher(testScheduler))

        assertEquals(Result.Error(NetworkError.SERVER_ERROR), repository.getStationsInArea(madrid))
        client.close()
    }
}

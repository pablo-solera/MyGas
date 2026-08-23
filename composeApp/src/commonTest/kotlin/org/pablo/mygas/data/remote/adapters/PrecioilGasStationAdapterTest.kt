package org.pablo.mygas.data.remote.adapters

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import org.pablo.mygas.data.remote.MunicipioService
import org.pablo.mygas.data.remote.responses.PrecioilGasStationResponse

class PrecioilGasStationAdapterTest {
    @Test
    fun `cached station details avoid duplicate requests within ttl`() = runTest {
        var requestCount = 0
        val payload = Json.encodeToString(station())
        val client = HttpClient(MockEngine) {
            engine {
                addHandler {
                    requestCount++
                    respond(
                        content = payload,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    )
                }
            }
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
        val adapter = PrecioilGasStationAdapter(MunicipioService(client))

        adapter.getById(42)
        adapter.getById(42)

        assertEquals(1, requestCount)
        client.close()
    }

    private fun station() = PrecioilGasStationResponse(
        idEstacion = 42,
        nombreEstacion = "Station",
        longitud = "-3.7",
        latitud = "40.4",
        margen = "",
        codPostal = "28001",
        direccion = "Street",
        horario = "24H",
        tipoVenta = "",
        idMunicipio = 1,
        lastUpdate = "",
        localidad = "Madrid",
        provincia = "Madrid",
        provinciaDistrito = "Madrid",
        marca = "Repsol"
    )
}

package org.pablo.mygas.data.remote

import io.ktor.client.engine.mock.respond
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.http.HttpStatusCode
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import org.pablo.mygas.core.utils.NetworkError
import org.pablo.mygas.core.utils.Result
import org.pablo.mygas.testing.jsonResponse
import org.pablo.mygas.testing.testClient
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class MunicipioServiceTest {

    @Test
    fun `malformed response is returned as serialization error`() = runTest {
        val client = testClient { jsonResponse("not-json") }
        try {
            assertEquals(Result.Error(NetworkError.SERIALIZATION), MunicipioService(client).getMunicipiosByProvincia("28"))
        } finally {
            client.close()
        }
    }

    @Test
    fun `cancellation is propagated instead of converted to an unknown error`() = runTest {
        val client = testClient { throw CancellationException("cancelled") }
        try {
            assertFailsWith<CancellationException> { MunicipioService(client).getMunicipiosByProvincia("28") }
        } finally {
            client.close()
        }
    }

    @Test
    fun `http status codes map to network errors`() = runTest {
        val expected = mapOf(
            401 to NetworkError.UNAUTHORIZED,
            403 to NetworkError.FORBIDDEN,
            404 to NetworkError.NOT_FOUND,
            408 to NetworkError.REQUEST_TIMEOUT,
            409 to NetworkError.CONFLICT,
            413 to NetworkError.PAYLOAD_TOO_LARGE,
            429 to NetworkError.TOO_MANY_REQUESTS,
            500 to NetworkError.SERVER_ERROR,
            503 to NetworkError.SERVER_ERROR,
            400 to NetworkError.UNKNOWN,
        )
        expected.forEach { (status, error) ->
            val client = testClient { respond("", HttpStatusCode.fromValue(status)) }
            try {
                assertEquals(Result.Error(error), MunicipioService(client).getMunicipiosByProvincia("28"), "status $status")
            } finally {
                client.close()
            }
        }
    }

    @Test
    fun `transport failures map to timeout or no internet`() = runTest {
        val cases: Map<Throwable, NetworkError> = mapOf(
            ConnectTimeoutException("connect", null) to NetworkError.REQUEST_TIMEOUT,
            UnresolvedAddressException() to NetworkError.NO_INTERNET,
            IOException("unreachable") to NetworkError.NO_INTERNET,
            IllegalStateException("boom") to NetworkError.UNKNOWN,
        )
        cases.forEach { (failure, error) ->
            val client = testClient { throw failure }
            try {
                assertEquals(Result.Error(error), MunicipioService(client).getMunicipiosByProvincia("28"), failure.toString())
            } finally {
                client.close()
            }
        }
    }

    @Test
    fun `ministry payload with decimal commas and missing optional fields decodes`() = runTest {
        val payload = """
            {"Fecha":"24/11/2026","Nota":null,"ResultadoConsulta":"OK","ListaEESSPrecio":[
              {"C.P.":"28001","Dirección":"CALLE X","Horario":"L-D: 24H","IDEESS":"1234","IDMunicipio":"4354",
               "IDProvincia":"28","Latitud":"40,416800","Localidad":"MADRID","Longitud (WGS84)":"-3,703800",
               "Municipio":"Madrid","Provincia":"MADRID","Rótulo":"REPSOL","Precio Gasolina 95 E5":"1,459",
               "Precio Gasoleo A":"1,389"},
              {"IDEESS":"99","Rótulo":"OTHER"}
            ]}
        """.trimIndent()
        val client = testClient { jsonResponse(payload) }
        try {
            val result = MunicipioService(client).getGasolinerasByMunicipio(4354)

            assertIs<Result.Success<*>>(result)
            val response = (result as Result.Success).data
            assertEquals(2, response.stations.size)
            assertEquals("1,459", response.stations.first().priceGasoline95E5)
        } finally {
            client.close()
        }
    }
}

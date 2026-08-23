package org.pablo.mygas.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.pablo.mygas.core.utils.NetworkError
import org.pablo.mygas.core.utils.Result

class MunicipioServiceTest {
    @Test
    fun `malformed response is returned as serialization error`() = runTest {
        val client = HttpClient(MockEngine) {
            engine {
                addHandler {
                    respond(
                        content = "not-json",
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    )
                }
            }
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }

        val service = MunicipioService(client)
        val result = service.getMunicipiosByProvincia("28")

        assertEquals(Result.Error(NetworkError.SERIALIZATION), result)
        client.close()
    }

    @Test
    fun `cancellation is propagated instead of converted to an unknown error`() = runTest {
        val client = HttpClient(MockEngine) {
            engine {
                addHandler { throw CancellationException("cancelled") }
            }
        }
        val service = MunicipioService(client)

        assertFailsWith<CancellationException> { service.getMunicipiosByProvincia("28") }
        client.close()
    }
}

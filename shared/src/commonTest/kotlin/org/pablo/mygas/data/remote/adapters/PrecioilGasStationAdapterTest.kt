package org.pablo.mygas.data.remote.adapters

import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.pablo.mygas.core.utils.NetworkError
import org.pablo.mygas.core.utils.NetworkException
import org.pablo.mygas.data.remote.MunicipioService
import org.pablo.mygas.data.remote.responses.PrecioilGasStationResponse
import org.pablo.mygas.testing.jsonResponse
import org.pablo.mygas.testing.testClient
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TestTimeSource

class PrecioilGasStationAdapterTest {
    private fun TestScope.supervisedScope() =
        CoroutineScope(SupervisorJob() + StandardTestDispatcher(testScheduler))

    private val payload = Json.encodeToString(
        PrecioilGasStationResponse(idEstacion = 42, idMunicipio = 1, marca = "Repsol")
    )

    @Test
    fun `cached station details avoid duplicate requests within ttl`() = runTest {
        var requests = 0
        val client = testClient { requests++; jsonResponse(payload) }
        val adapter = PrecioilGasStationAdapter(MunicipioService(client), TestTimeSource(), scope = supervisedScope())

        adapter.getById(42)
        adapter.getById(42)

        assertEquals(1, requests)
        client.close()
    }

    @Test
    fun `entries are fetched again once the ttl has passed`() = runTest {
        var requests = 0
        val time = TestTimeSource()
        val client = testClient { requests++; jsonResponse(payload) }
        val adapter = PrecioilGasStationAdapter(MunicipioService(client), time, scope = supervisedScope())

        adapter.getById(42)
        time += 6.minutes
        adapter.getById(42)

        assertEquals(2, requests)
        client.close()
    }

    @Test
    fun `failures keep the network error and are retried`() = runTest {
        var requests = 0
        val client = testClient {
            requests++
            if (requests == 1) jsonResponse("", HttpStatusCode.NotFound) else jsonResponse(payload)
        }
        val adapter = PrecioilGasStationAdapter(MunicipioService(client), TestTimeSource(), scope = supervisedScope())

        val failure = assertFailsWith<NetworkException> { adapter.getById(42) }
        assertEquals(NetworkError.NOT_FOUND, failure.error)

        assertEquals(42, adapter.getById(42).idEstacion)
        assertEquals(2, requests)
        client.close()
    }
}

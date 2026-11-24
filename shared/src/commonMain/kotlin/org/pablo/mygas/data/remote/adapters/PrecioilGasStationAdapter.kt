package org.pablo.mygas.data.remote.adapters

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.pablo.mygas.core.utils.NetworkException
import org.pablo.mygas.core.utils.Result
import org.pablo.mygas.data.remote.MunicipioService
import org.pablo.mygas.data.remote.datasources.PrecioilGasStationDataSource
import org.pablo.mygas.data.remote.responses.PrecioilGasStationResponse
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TimeMark
import kotlin.time.TimeSource

class PrecioilGasStationAdapter(
    private val api: MunicipioService,
    private val timeSource: TimeSource = TimeSource.Monotonic,
    private val cacheTtl: Duration = 5.minutes,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) : PrecioilGasStationDataSource {
    private data class CachedStation(
        val response: PrecioilGasStationResponse,
        val expiresAt: TimeMark
    )

    private val mutex = Mutex()
    private val cache = mutableMapOf<Int, CachedStation>()
    private val inFlight = mutableMapOf<Int, Deferred<PrecioilGasStationResponse>>()

    /** @throws NetworkException when the request fails. */
    override suspend fun getById(gasStationId: Int): PrecioilGasStationResponse {
        mutex.withLock {
            cache[gasStationId]
                ?.takeIf { it.expiresAt.hasNotPassedNow() }
                ?.response
        }?.let { return it }

        val request = mutex.withLock {
            inFlight[gasStationId] ?: scope.async(start = CoroutineStart.LAZY) {
                when (val result = api.getGasolineraById(gasStationId)) {
                    is Result.Success -> result.data
                    is Result.Error -> throw NetworkException(result.error)
                }
            }.also { inFlight[gasStationId] = it }
        }

        return try {
            request.await().also { response ->
                mutex.withLock {
                    cache[gasStationId] = CachedStation(
                        response = response,
                        expiresAt = timeSource.markNow() + cacheTtl
                    )
                }
            }
        } finally {
            mutex.withLock {
                if (inFlight[gasStationId] === request) {
                    inFlight.remove(gasStationId)
                }
            }
        }
    }
}

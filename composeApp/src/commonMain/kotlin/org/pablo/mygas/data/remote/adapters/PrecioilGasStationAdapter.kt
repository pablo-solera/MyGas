package org.pablo.mygas.data.remote.adapters

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TimeMark
import kotlin.time.TimeSource
import org.pablo.mygas.core.utils.Result
import org.pablo.mygas.data.remote.MunicipioService
import org.pablo.mygas.data.remote.datasources.PrecioilGasStationDataSource
import org.pablo.mygas.data.remote.responses.PrecioilGasStationResponse

class PrecioilGasStationAdapter(
    private val api: MunicipioService
) : PrecioilGasStationDataSource {
    private data class CachedStation(
        val response: PrecioilGasStationResponse,
        val expiresAt: TimeMark
    )

    private val mutex = Mutex()
    private val cache = mutableMapOf<Int, CachedStation>()
    private val inFlight = mutableMapOf<Int, kotlinx.coroutines.Deferred<PrecioilGasStationResponse>>()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

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
                    is Result.Error -> error("Unable to load gas station $gasStationId: ${result.error}")
                }
            }.also { inFlight[gasStationId] = it }
        }

        return try {
            request.await().also { response ->
                mutex.withLock {
                    cache[gasStationId] = CachedStation(
                        response = response,
                        expiresAt = TimeSource.Monotonic.markNow() + 5.minutes
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

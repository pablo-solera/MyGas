package org.pablo.mygas.data.repositories

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.pablo.mygas.core.utils.NetworkError
import org.pablo.mygas.core.utils.NetworkException
import org.pablo.mygas.core.utils.Result
import org.pablo.mygas.data.remote.MunicipioService
import org.pablo.mygas.data.remote.datasources.PrecioilGasStationDataSource
import org.pablo.mygas.data.toDomain
import org.pablo.mygas.domain.models.GasStation
import org.pablo.mygas.domain.models.map.MapBounds
import org.pablo.mygas.domain.models.withAveragePrices
import org.pablo.mygas.domain.repositories.GasStationRepository
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TimeMark
import kotlin.time.TimeSource

/**
 * The ministry has no bounding-box endpoint, so for area searches the whole national list is
 * downloaded once, kept in memory and filtered locally. A stale copy is served if a refresh fails.
 */
class GasStationRepositoryImpl(
    private val service: MunicipioService,
    private val detailsSource: PrecioilGasStationDataSource,
    private val timeSource: TimeSource = TimeSource.Monotonic,
    private val cacheTtl: Duration = 30.minutes,
    private val cpuDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : GasStationRepository {

    private class Snapshot(val stations: List<GasStation>, val expiresAt: TimeMark)

    private val mutex = Mutex()
    private var snapshot: Snapshot? = null

    override suspend fun getStationsInArea(bounds: MapBounds): Result<List<GasStation>, NetworkError> {
        val stations = when (val result = loadAll()) {
            is Result.Success -> result.data
            is Result.Error -> return result
        }
        val inArea = withContext(cpuDispatcher) {
            stations.filter { bounds.contains(it.location.latitude, it.location.longitude) }
                .withAveragePrices()
        }
        return Result.Success(inArea)
    }

    override suspend fun getStationsByMunicipality(municipalityId: Int): Result<List<GasStation>, NetworkError> =
        when (val result = service.getGasolinerasByMunicipio(municipalityId)) {
            is Result.Success -> Result.Success(
                withContext(cpuDispatcher) { result.data.stations.toDomain().withAveragePrices() }
            )
            is Result.Error -> result
        }

    override suspend fun getStation(id: Int): Result<GasStation, NetworkError> =
        try {
            Result.Success(detailsSource.getById(id).toDomain())
        } catch (e: CancellationException) {
            throw e
        } catch (e: NetworkException) {
            Result.Error(e.error)
        } catch (_: Exception) {
            Result.Error(NetworkError.UNKNOWN)
        }

    private suspend fun loadAll(): Result<List<GasStation>, NetworkError> = mutex.withLock {
        val current = snapshot
        if (current != null && current.expiresAt.hasNotPassedNow()) {
            return Result.Success(current.stations)
        }
        when (val response = service.getAllGasolineras()) {
            is Result.Success -> {
                val stations = withContext(cpuDispatcher) {
                    response.data.stations.toDomain().filter { it.hasValidLocation }
                }
                snapshot = Snapshot(stations, timeSource.markNow() + cacheTtl)
                Result.Success(stations)
            }
            is Result.Error ->
                if (current != null) Result.Success(current.stations) else Result.Error(response.error)
        }
    }
}

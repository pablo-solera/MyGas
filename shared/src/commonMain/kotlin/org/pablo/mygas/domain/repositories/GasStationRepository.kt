package org.pablo.mygas.domain.repositories

import org.pablo.mygas.core.utils.NetworkError
import org.pablo.mygas.core.utils.Result
import org.pablo.mygas.domain.models.GasStation
import org.pablo.mygas.domain.models.map.MapBounds

interface GasStationRepository {
    /** Stations located inside [bounds], with average prices computed over that set. */
    suspend fun getStationsInArea(bounds: MapBounds): Result<List<GasStation>, NetworkError>
}

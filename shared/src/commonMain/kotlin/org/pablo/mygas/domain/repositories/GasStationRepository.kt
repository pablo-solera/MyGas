package org.pablo.mygas.domain.repositories

import org.pablo.mygas.core.utils.NetworkError
import org.pablo.mygas.core.utils.Result
import org.pablo.mygas.domain.models.GasStation
import org.pablo.mygas.domain.models.map.MapBounds

interface GasStationRepository {
    /** Stations located inside [bounds], with average prices computed over that set. */
    suspend fun getStationsInArea(bounds: MapBounds): Result<List<GasStation>, NetworkError>

    /** Stations of one municipality, with average prices computed over that set. */
    suspend fun getStationsByMunicipality(municipalityId: Int): Result<List<GasStation>, NetworkError>

    suspend fun getStation(id: Int): Result<GasStation, NetworkError>
}

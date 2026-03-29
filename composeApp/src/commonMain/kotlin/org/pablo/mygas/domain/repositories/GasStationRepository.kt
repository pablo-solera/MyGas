package org.pablo.mygas.domain.repositories

import kotlinx.coroutines.flow.Flow
import org.pablo.mygas.domain.models.GasStation

interface GasStationRepository {
    suspend fun getGasStations(): List<GasStation>
    fun observeGasStations(): Flow<List<GasStation>>
    suspend fun addFavorite(gasStationId: Int)
    suspend fun removeFavorite(gasStationId: Int)

}
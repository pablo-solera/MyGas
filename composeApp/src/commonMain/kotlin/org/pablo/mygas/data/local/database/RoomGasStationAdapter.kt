package org.pablo.mygas.data.local.database

import kotlinx.coroutines.flow.Flow
import org.pablo.mygas.data.local.database.models.GasStationEntity

class RoomGasStationAdapter(
    private val dao: GasStationDao
): GasStationLocalDataSource {


    override fun observeFavoriteIds(): Flow<List<Int>> {
        return dao.observeFavoriteIds()
    }

    override suspend fun getAllFavoriteIds(): List<Int> {
        return dao.getAllFavoriteIds()
    }

    override suspend fun addFavorite(id: Int) {
        dao.insert(GasStationEntity(id))
    }

    override suspend fun removeFavorite(id: Int) {
        dao.deleteById(id)
    }
}
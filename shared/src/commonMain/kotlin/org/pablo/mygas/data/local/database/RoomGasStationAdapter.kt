package org.pablo.mygas.data.local.database

import kotlinx.coroutines.flow.Flow
import org.pablo.mygas.data.local.database.models.GasStationEntity
import org.pablo.mygas.domain.repositories.FavoritesRepository

class RoomGasStationAdapter(
    private val dao: GasStationDao
) : FavoritesRepository {

    override fun observeFavoriteIds(): Flow<List<Int>> = dao.observeFavoriteIds()

    override suspend fun addFavorite(id: Int) {
        dao.insert(GasStationEntity(id))
    }

    override suspend fun removeFavorite(id: Int) {
        dao.deleteById(id)
    }
}

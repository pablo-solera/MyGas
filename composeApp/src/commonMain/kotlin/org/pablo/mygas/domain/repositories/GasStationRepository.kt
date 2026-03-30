package org.pablo.mygas.domain.repositories

import kotlinx.coroutines.flow.Flow
import org.pablo.mygas.domain.models.GasStation

interface GasStationRepository {
    suspend fun getAll(): List<GasStation>
    suspend fun getById(id: Int): GasStation?
    suspend fun getByMunicipio(municipioId: Int): List<GasStation>
    suspend fun getByProvince(provinceId: Int): List<GasStation>
    fun observeAllFavorites(): Flow<List<Int>>
    suspend fun addFavorite(gasStationId: Int)
    suspend fun removeFavorite(gasStationId: Int)

}
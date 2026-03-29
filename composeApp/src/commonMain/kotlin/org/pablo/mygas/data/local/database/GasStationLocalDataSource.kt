package org.pablo.mygas.data.local.database

import kotlinx.coroutines.flow.Flow

interface GasStationLocalDataSource {
    fun observeFavoriteIds(): Flow<List<Int>>
    suspend fun getAllFavoriteIds(): List<Int>
    suspend fun addFavorite(id: Int)
    suspend fun removeFavorite(id: Int)
}
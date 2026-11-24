package org.pablo.mygas.domain.repositories

import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun observeFavoriteIds(): Flow<List<Int>>
    suspend fun addFavorite(id: Int)
    suspend fun removeFavorite(id: Int)
}

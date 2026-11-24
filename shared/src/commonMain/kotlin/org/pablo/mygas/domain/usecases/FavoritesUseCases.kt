package org.pablo.mygas.domain.usecases

import kotlinx.coroutines.flow.Flow
import org.pablo.mygas.domain.repositories.FavoritesRepository

class ObserveFavoriteIdsUseCase(private val repository: FavoritesRepository) {
    operator fun invoke(): Flow<List<Int>> = repository.observeFavoriteIds()
}

class SetFavoriteUseCase(private val repository: FavoritesRepository) {
    suspend operator fun invoke(id: Int, favorite: Boolean) {
        if (favorite) repository.addFavorite(id) else repository.removeFavorite(id)
    }
}

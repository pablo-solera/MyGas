package org.pablo.mygas.domain.usecases

import org.pablo.mygas.core.utils.NetworkError
import org.pablo.mygas.core.utils.Result
import org.pablo.mygas.domain.models.GasStation
import org.pablo.mygas.domain.models.map.MapBounds
import org.pablo.mygas.domain.repositories.GasStationRepository

class GetStationsInAreaUseCase(
    private val repository: GasStationRepository,
) {
    suspend operator fun invoke(bounds: MapBounds): Result<List<GasStation>, NetworkError> =
        repository.getStationsInArea(bounds)
}

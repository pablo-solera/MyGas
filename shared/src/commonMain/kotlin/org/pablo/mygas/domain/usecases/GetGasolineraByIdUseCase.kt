package org.pablo.mygas.domain.usecases

import org.pablo.mygas.data.remote.responses.PrecioilGasStationResponse
import org.pablo.mygas.data.remote.datasources.GovernmentGasStationDataSource
import org.pablo.mygas.core.utils.NetworkError
import org.pablo.mygas.core.utils.Result

class GetGasolineraByIdUseCase(
    private val repository: GovernmentGasStationDataSource
) {
    suspend operator fun invoke(gasStationId: Int): Result<PrecioilGasStationResponse, NetworkError> {
        return repository.getGasolineraById(gasStationId)
    }
}

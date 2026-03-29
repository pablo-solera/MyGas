package org.pablo.mygas.domain.usecases

import org.pablo.mygas.data.remote.repository.GasolinerasRepository
import org.pablo.mygas.data.remote.responses.GasStationResponse
import org.pablo.mygas.core.utils.NetworkError
import org.pablo.mygas.core.utils.Result

class GetGasolineraByIdUseCase(
    private val repository: GasolinerasRepository
) {
    suspend operator fun invoke(gasStationId: Int): Result<GasStationResponse, NetworkError> {
        return repository.getGasolineraById(gasStationId)
    }
}
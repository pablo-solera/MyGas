package org.pablo.mygas.domain.usecases

import org.pablo.mygas.data.remote.responses.ComunidadAutonomaResponse
import org.pablo.mygas.data.remote.repository.GasolinerasRepository
import org.pablo.mygas.core.utils.NetworkError
import org.pablo.mygas.core.utils.Result

class GetAllComunidadesAutonomasUseCase(
    private val repository: GasolinerasRepository
) {
    suspend operator fun invoke(): Result<List<ComunidadAutonomaResponse>, NetworkError> {
        return repository.getComunidadesAutonomas()
    }
}
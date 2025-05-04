package org.pablo.mygas.domain

import org.pablo.mygas.data.remote.responses.ComunidadAutonomaResponse
import org.pablo.mygas.data.remote.GasolinerasRepository
import org.pablo.mygas.utils.NetworkError
import org.pablo.mygas.utils.Result

class GetAllComunidadesAutonomasUseCase(
    private val repository: GasolinerasRepository
) {
    suspend operator fun invoke(): Result<List<ComunidadAutonomaResponse>, NetworkError> {
        return repository.getComunidadesAutonomas()
    }
}
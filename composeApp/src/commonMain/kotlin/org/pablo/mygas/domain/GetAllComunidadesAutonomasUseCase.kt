package org.pablo.mygas.domain

import org.pablo.mygas.data.ComunidadAutonoma
import org.pablo.mygas.data.GasolinerasRepository
import org.pablo.mygas.utils.NetworkError
import org.pablo.mygas.utils.Result

class GetAllComunidadesAutonomasUseCase(
    private val repository: GasolinerasRepository
) {
    suspend operator fun invoke(): Result<List<ComunidadAutonoma>, NetworkError> {
        return repository.getComunidadesAutonomas2()
    }
}
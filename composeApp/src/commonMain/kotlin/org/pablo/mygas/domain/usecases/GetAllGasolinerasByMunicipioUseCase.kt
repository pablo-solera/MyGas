package org.pablo.mygas.domain.usecases

import org.pablo.mygas.data.remote.repository.GasolinerasRepository
import org.pablo.mygas.data.remote.responses.GasolinerasResponse
import org.pablo.mygas.core.utils.NetworkError
import org.pablo.mygas.core.utils.Result

class GetAllGasolinerasByMunicipioUseCase(
    private val repository: GasolinerasRepository
) {
    suspend operator fun invoke(idMunicipio: Int): Result<GasolinerasResponse, NetworkError> {
        return repository.getGasolinerasByMunicipio(idMunicipio)
    }
}
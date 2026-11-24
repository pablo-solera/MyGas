package org.pablo.mygas.domain.usecases

import org.pablo.mygas.data.remote.responses.GasolinerasResponse
import org.pablo.mygas.data.remote.datasources.GovernmentGasStationDataSource
import org.pablo.mygas.core.utils.NetworkError
import org.pablo.mygas.core.utils.Result

class GetAllGasolinerasByMunicipioUseCase(
    private val repository: GovernmentGasStationDataSource
) {
    suspend operator fun invoke(idMunicipio: Int): Result<GasolinerasResponse, NetworkError> {
        return repository.getGasolinerasByMunicipio(idMunicipio)
    }
}

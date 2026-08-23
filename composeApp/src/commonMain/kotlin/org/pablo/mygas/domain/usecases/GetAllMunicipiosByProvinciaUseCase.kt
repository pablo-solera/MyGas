package org.pablo.mygas.domain.usecases

import org.pablo.mygas.data.remote.MunicipioItem
import org.pablo.mygas.data.remote.datasources.GovernmentGasStationDataSource
import org.pablo.mygas.core.utils.NetworkError
import org.pablo.mygas.core.utils.Result

class GetAllMunicipiosByProvinciaUseCase(
    private val repository: GovernmentGasStationDataSource
) {
    suspend operator fun invoke(idProvincia: String): Result<List<MunicipioItem>, NetworkError> {
        return repository.getMunicipiosByProvincia(idProvincia)
    }
}

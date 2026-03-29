package org.pablo.mygas.domain.usecases

import org.pablo.mygas.data.remote.repository.GasolinerasRepository
import org.pablo.mygas.data.remote.MunicipioItem
import org.pablo.mygas.core.utils.NetworkError
import org.pablo.mygas.core.utils.Result

class GetAllMunicipiosByProvinciaUseCase(
    private val repository: GasolinerasRepository
) {
    suspend operator fun invoke(idProvincia: Int): Result<List<MunicipioItem>, NetworkError> {
        return repository.getMunicipiosByProvincia(idProvincia)
    }
}
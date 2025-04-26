package org.pablo.mygas.domain

import org.pablo.mygas.data.GasolinerasRepository
import org.pablo.mygas.data.MunicipioItem
import org.pablo.mygas.utils.NetworkError
import org.pablo.mygas.utils.Result

class GetAllMunicipiosByProvinciaUseCase(
    private val repository: GasolinerasRepository
) {
    suspend operator fun invoke(idProvincia: Int): Result<List<MunicipioItem>, NetworkError> {
        return repository.getMunicipiosByProvincia(idProvincia)
    }
}
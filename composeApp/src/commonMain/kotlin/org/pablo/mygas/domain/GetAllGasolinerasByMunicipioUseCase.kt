package org.pablo.mygas.domain

import org.pablo.mygas.data.GasolinerasRepository
import org.pablo.mygas.data.GasolinerasResponse
import org.pablo.mygas.data.ListaEESSPrecio
import org.pablo.mygas.utils.NetworkError
import org.pablo.mygas.utils.Result

class GetAllGasolinerasByMunicipioUseCase(
    private val repository: GasolinerasRepository
) {
    suspend operator fun invoke(idMunicipio: Int): Result<GasolinerasResponse, NetworkError> {
        return repository.getGasolinerasByMunicipio2(idMunicipio)
    }
}
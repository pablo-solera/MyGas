package org.pablo.mygas.domain.usecases

import org.pablo.mygas.core.utils.NetworkError
import org.pablo.mygas.core.utils.Result
import org.pablo.mygas.domain.models.Municipality
import org.pablo.mygas.domain.repositories.LocationRepository

class GetMunicipalitiesUseCase(private val repository: LocationRepository) {
    suspend operator fun invoke(provinceId: String): Result<List<Municipality>, NetworkError> =
        repository.getMunicipalities(provinceId)
}

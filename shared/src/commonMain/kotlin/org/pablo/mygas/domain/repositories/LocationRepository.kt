package org.pablo.mygas.domain.repositories

import org.pablo.mygas.core.utils.NetworkError
import org.pablo.mygas.core.utils.Result
import org.pablo.mygas.domain.models.Municipality

interface LocationRepository {
    suspend fun getMunicipalities(provinceId: String): Result<List<Municipality>, NetworkError>
}

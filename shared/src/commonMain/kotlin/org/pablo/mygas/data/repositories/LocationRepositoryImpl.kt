package org.pablo.mygas.data.repositories

import org.pablo.mygas.core.utils.NetworkError
import org.pablo.mygas.core.utils.Result
import org.pablo.mygas.core.utils.map
import org.pablo.mygas.data.remote.MunicipioService
import org.pablo.mygas.domain.models.Municipality
import org.pablo.mygas.domain.repositories.LocationRepository

class LocationRepositoryImpl(
    private val service: MunicipioService,
) : LocationRepository {

    override suspend fun getMunicipalities(provinceId: String): Result<List<Municipality>, NetworkError> =
        service.getMunicipiosByProvincia(provinceId).map { items ->
            items.mapNotNull { item ->
                val id = item.idMunicipio.trim().toIntOrNull() ?: return@mapNotNull null
                Municipality(id = id, name = item.municipio, provinceId = item.idProvincia)
            }
        }
}

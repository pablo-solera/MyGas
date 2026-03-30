package org.pablo.mygas.domain.repositories

import org.pablo.mygas.domain.models.Municipality
import org.pablo.mygas.domain.models.Province

interface LocationRepository {
    suspend fun getProvinces(): List<Province>
    suspend fun getMunicipalitiesByProvince(provinceId: Int): List<Municipality>
    suspend fun getMunicipalityById(municipalityId: Int): Municipality?

}
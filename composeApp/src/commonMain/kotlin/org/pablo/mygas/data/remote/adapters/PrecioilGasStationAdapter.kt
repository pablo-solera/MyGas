package org.pablo.mygas.data.remote.adapters

import org.pablo.mygas.core.utils.NetworkError
import org.pablo.mygas.core.utils.onError
import org.pablo.mygas.core.utils.onSuccess
import org.pablo.mygas.data.remote.MunicipioService
import org.pablo.mygas.data.remote.datasources.PrecioilGasStationDataSource
import org.pablo.mygas.data.remote.responses.PrecioilGasStationResponse

class PrecioilGasStationAdapter(
    private val api: MunicipioService
) : PrecioilGasStationDataSource {
    override suspend fun getById(gasStationId: Int): PrecioilGasStationResponse {
        lateinit var result: PrecioilGasStationResponse

        api.getGasolineraById(gasStationId)
            .onSuccess { response ->
                result = response
            }.onError {
                NetworkError.SERVER_ERROR
            }
        return result

    }


}
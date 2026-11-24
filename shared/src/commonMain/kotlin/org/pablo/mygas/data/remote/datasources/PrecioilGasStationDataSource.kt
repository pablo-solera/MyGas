package org.pablo.mygas.data.remote.datasources

import org.pablo.mygas.data.remote.responses.PrecioilGasStationResponse

interface PrecioilGasStationDataSource {

    suspend fun getById(gasStationId: Int): PrecioilGasStationResponse
}
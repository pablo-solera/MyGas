package org.pablo.mygas.data.remote.datasources

import org.pablo.mygas.data.remote.responses.GasStationResponse

interface PrecioilGasStationDataSource {

    suspend fun getById(gasStationId: Int): GasStationResponse
}
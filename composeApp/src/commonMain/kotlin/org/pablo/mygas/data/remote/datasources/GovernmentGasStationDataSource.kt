package org.pablo.mygas.data.remote.datasources

import org.pablo.mygas.core.utils.NetworkError
import org.pablo.mygas.core.utils.Result
import org.pablo.mygas.data.remote.MunicipioItem
import org.pablo.mygas.data.remote.responses.ComunidadAutonomaResponse
import org.pablo.mygas.data.remote.responses.PrecioilGasStationResponse
import org.pablo.mygas.data.remote.responses.GasolinerasResponse

interface GovernmentGasStationDataSource {

    suspend fun getGasolinerasByMunicipio(municipioId: Int): Result<GasolinerasResponse, NetworkError>

    suspend fun getComunidadesAutonomas(): org.pablo.mygas.core.utils.Result<List<ComunidadAutonomaResponse>, NetworkError>

    suspend fun getMunicipiosByProvincia(provinciaId: Int): org.pablo.mygas.core.utils.Result<List<MunicipioItem>, NetworkError>

    suspend fun getGasolineraById(id: Int): Result<PrecioilGasStationResponse, NetworkError>
}
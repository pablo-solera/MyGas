package org.pablo.mygas.data.remote.responses

import kotlinx.serialization.Serializable
import org.pablo.mygas.data.remote.GovernmentGasStationResponse

@Serializable
data class GasolinerasResponse(
    val Fecha: String,
    val ListaEESSPrecio: List<GovernmentGasStationResponse>,
    val Nota: String,
    val ResultadoConsulta: String
)
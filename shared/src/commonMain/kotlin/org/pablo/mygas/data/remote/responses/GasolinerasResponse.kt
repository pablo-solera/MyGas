package org.pablo.mygas.data.remote.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.pablo.mygas.data.remote.GovernmentGasStationResponse

@Serializable
data class GasolinerasResponse(
    @SerialName("Fecha") val date: String = "",
    @SerialName("ListaEESSPrecio") val stations: List<GovernmentGasStationResponse> = emptyList(),
    @SerialName("Nota") val note: String? = null,
    @SerialName("ResultadoConsulta") val result: String = "",
)

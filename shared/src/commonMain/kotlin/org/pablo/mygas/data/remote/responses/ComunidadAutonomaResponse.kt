package org.pablo.mygas.data.remote.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ComunidadAutonomaResponse(

    @SerialName("CCAA")
    val ccaa: String,

    @SerialName("IDCCAA")
    val idccaa: String
)
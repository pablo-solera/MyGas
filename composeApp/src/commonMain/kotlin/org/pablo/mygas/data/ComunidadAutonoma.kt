package org.pablo.mygas.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ComunidadAutonoma(

    @SerialName("CCAA")
    val ccaa: String,

    @SerialName("IDCCAA")
    val idccaa: String
)
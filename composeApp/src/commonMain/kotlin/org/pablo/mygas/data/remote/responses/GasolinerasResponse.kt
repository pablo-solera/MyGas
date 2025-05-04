package org.pablo.mygas.data.remote.responses

import kotlinx.serialization.Serializable
import org.pablo.mygas.data.remote.ListaEESSPrecio

@Serializable
data class GasolinerasResponse(
    val Fecha: String,
    val ListaEESSPrecio: List<ListaEESSPrecio>,
    val Nota: String,
    val ResultadoConsulta: String
)
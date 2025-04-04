package org.pablo.mygas.data

import kotlinx.serialization.Serializable

@Serializable
data class GasolinerasResponse(
    val Fecha: String,
    val ListaEESSPrecio: List<ListaEESSPrecio>,
    val Nota: String,
    val ResultadoConsulta: String
)
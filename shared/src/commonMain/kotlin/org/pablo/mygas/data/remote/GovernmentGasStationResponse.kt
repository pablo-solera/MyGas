package org.pablo.mygas.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("ListaEESSPrecio")
data class GovernmentGasStationResponse(
   // val % BioEtanol: String,
   // val % Éster metílico: String,

    @SerialName("C.P.")
    val cp: String,
    val Dirección: String,
    val Horario: String,
    val IDCCAA: String,
    val IDEESS: String,
    val IDMunicipio: String,
    val IDProvincia: String,
    val Latitud: String,
    val Localidad: String,

    @SerialName("Longitud (WGS84)")
    val longitud: String,
    val Margen: String,
    val Municipio: String,
    // val Precio Biodiesel: String,
    // val Precio Bioetanol: String,
    // val Precio Gas Natural Comprimido: String,
    // val Precio Gas Natural Licuado: String,
    // val Precio Gases licuados del petróleo: String,

    @SerialName("Precio Gasoleo A")
    val precioGasoleoA: String? = null,
    // val Precio Gasoleo B: String,
    // val Precio Gasoleo Premium: String,
    // val Precio Gasolina 95 E10: String,

    @SerialName("Precio Gasolina 95 E5")
    val precioGasolina95E5: String? = null,
    // val Precio Gasolina 95 E5 Premium: String,
    // val Precio Gasolina 98 E10: String,
    @SerialName("Precio Gasolina 98 E5")
    val precioGasolina98E5: String? = null,
    // val Precio Hidrogeno: String,
    val Provincia: String,
    val Remisión: String,

    @SerialName("Rótulo")
    val rotulo: String,
    // val Tipo Venta: String
)

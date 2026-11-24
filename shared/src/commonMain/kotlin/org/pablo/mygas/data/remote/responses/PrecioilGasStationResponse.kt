package org.pablo.mygas.data.remote.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PrecioilGasStationResponse(

    @SerialName("idEstacion")
    val idEstacion: Int,

    @SerialName("nombreEstacion")
    val nombreEstacion: String,

    @SerialName("longitud")
    val longitud: String,

    @SerialName("latitud")
    val latitud: String,

    @SerialName("margen")
    val margen: String,

    @SerialName("codPostal")
    val codPostal: String,

    @SerialName("direccion")
    val direccion: String,

    @SerialName("horario")
    val horario: String,

    @SerialName("tipoVenta")
    val tipoVenta: String,

    @SerialName("idMunicipio")
    val idMunicipio: Int,

    @SerialName("lastUpdate")
    val lastUpdate: String,

    @SerialName("localidad")
    val localidad: String,

    @SerialName("Gasolina95")
    val gasolina95: String? = null,

    @SerialName("Gasolina95_media")
    val gasolina95Media: String? = null,

    @SerialName("Gasolina98")
    val gasolina98: String? = null,

    @SerialName("Gasolina98_media")
    val gasolina98Media: String? = null,

    @SerialName("Diesel")
    val diesel: String? = null,

    @SerialName("Diesel_media")
    val dieselMedia: String? = null,

    @SerialName("DieselPremium")
    val dieselPremium: String? = null,

    @SerialName("DieselPremium_media")
    val dieselPremiumMedia: String? = null,

    @SerialName("DieselB_media")
    val dieselBMedia: String? = null,

    @SerialName("GLP")
    val glp: String? = null,

    @SerialName("GLP_media")
    val glpMedia: String? = null,

    @SerialName("provincia")
    val provincia: String,

    @SerialName("provinciaDistrito")
    val provinciaDistrito: String,

    @SerialName("marca")
    val marca: String,

    @SerialName("HVO_media")
    val hvoMedia: String? = null,

    @SerialName("HVO")
    val hvo: String? = null
)
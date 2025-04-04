package org.pablo.mygas.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MunicipioItem(

    @SerialName("CCAA")
    val ccaa: String,

    @SerialName("IDCCAA")
    val idCcaa: String,

    @SerialName("IDMunicipio")
    val idMunicipio: String,

    @SerialName("IDProvincia")
    val idProvincia: String,

    @SerialName("Municipio")
    val municipio: String,

    @SerialName("Provincia")
    val provincia: String
) {
    fun toDomain() = MunicipioItem(
        ccaa = ccaa,
        idCcaa = idCcaa,
        idMunicipio = idMunicipio,
        idProvincia = idProvincia,
        municipio = municipio,
        provincia = provincia
    )
}
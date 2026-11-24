package org.pablo.mygas.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GovernmentGasStationResponse(
    @SerialName("C.P.") val postalCode: String = "",
    @SerialName("Dirección") val address: String = "",
    @SerialName("Horario") val openingHours: String = "",
    @SerialName("IDEESS") val stationId: String = "",
    @SerialName("IDMunicipio") val municipalityId: String = "",
    @SerialName("IDProvincia") val provinceId: String = "",
    @SerialName("Latitud") val latitude: String = "",
    @SerialName("Localidad") val locality: String = "",
    @SerialName("Longitud (WGS84)") val longitude: String = "",
    @SerialName("Municipio") val municipality: String = "",
    @SerialName("Provincia") val province: String = "",
    @SerialName("Rótulo") val label: String = "",
    @SerialName("Precio Gasoleo A") val priceDiesel: String? = null,
    @SerialName("Precio Gasoleo Premium") val priceDieselPremium: String? = null,
    @SerialName("Precio Gasolina 95 E5") val priceGasoline95E5: String? = null,
    @SerialName("Precio Gasolina 95 E10") val priceGasoline95E10: String? = null,
    @SerialName("Precio Gasolina 98 E5") val priceGasoline98E5: String? = null,
    @SerialName("Precio Gasolina 98 E10") val priceGasoline98E10: String? = null,
    @SerialName("Precio Biodiesel") val priceBiodiesel: String? = null,
    @SerialName("Precio Gases licuados del petróleo") val priceGlp: String? = null,
)

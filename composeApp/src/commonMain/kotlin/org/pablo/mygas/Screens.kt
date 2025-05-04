package org.pablo.mygas

import kotlinx.serialization.Serializable


@Serializable
object HomeGraph

@Serializable
object FavoriteGraph

@Serializable
object ProfileGraph

@Serializable
data class GasStationDetail(
    val location: String,
    val latitude: String,
    val longitude: String,
    val dieselPrice: String,
    val gasolinePrice: String,
    val rotulo: String
)

@Serializable
object Home

@Serializable
object Favorite
package org.pablo.mygas.core.navigation

import kotlinx.serialization.Serializable


@Serializable
object HomeGraph

@Serializable
object FavoriteGraph

@Serializable
object SettingsGraph

@Serializable
data class GasStationDetail(
    val id: String,
    val location: String,
    val latitude: String,
    val longitude: String,
    val dieselPrice: String,
    val gasolinePrice: String,
    val rotulo: String,
    val horario: String
)

@Serializable
object Home

@Serializable
object Favorite

@Serializable
object Settings
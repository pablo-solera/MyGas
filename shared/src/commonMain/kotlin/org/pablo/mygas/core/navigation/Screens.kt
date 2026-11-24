package org.pablo.mygas.core.navigation

import kotlinx.serialization.Serializable


@Serializable
object HomeGraph

@Serializable
object MapGraph

@Serializable
object FavoriteGraph

@Serializable
object SettingsGraph

@Serializable
data class GasStationDetail(
    val id: Int,
)

@Serializable
object Home

@Serializable
object Favorite

@Serializable
object Map

@Serializable
object Settings
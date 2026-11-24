package org.pablo.mygas.domain.models.map

data class MapProperties (
    val isBuildingEnabled: Boolean = false,
    val isIndoorEnabled: Boolean = false,
    val isMyLocationEnabled: Boolean = false,
    val isTrafficEnabled: Boolean = false,
    val mapType: MapType = MapType.NORMAL,
    val maxZoomPreference: Float = 21.0f,
    val minZoomPreference: Float = 3.0f
)
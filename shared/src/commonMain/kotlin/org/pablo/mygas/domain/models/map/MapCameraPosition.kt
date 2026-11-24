package org.pablo.mygas.domain.models.map

data class MapCameraPosition(
    val coordinates: MapCoordinate,
    val zoom: Float = 14f,
)

package org.pablo.mygas.domain.models.map

data class MapState(
    val cameraPosition: MapCameraPosition = MapCameraPosition(
        coordinates = MapCoordinate(40.4817, -3.3644),
        zoom = 12f
    ),
    val markers: List<MapMarker> = emptyList(),
    val settings: MapSettings = MapSettings(),
)

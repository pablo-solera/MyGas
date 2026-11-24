package org.pablo.mygas.domain.models.map

/**
 * What a platform map needs to draw. [cameraPosition] is only the initial camera: maps
 * keep their own camera afterwards and report it through the viewport callback.
 */
data class MapState(
    val cameraPosition: MapCameraPosition = MapCameraPosition(
        coordinates = MapCoordinate(40.4168, -3.7038),
        zoom = 13f
    ),
    val markers: List<MapMarker> = emptyList(),
    val settings: MapSettings = MapSettings(),
)

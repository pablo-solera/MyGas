package org.pablo.mygas.domain.models.map

import org.pablo.mygas.ui.state.Loadable

data class MapState(
    val cameraPosition: MapCameraPosition = MapCameraPosition(
        coordinates = MapCoordinate(0.0, 0.0),
        zoom = 14f
    ),
    val markers: List<MapMarker> = emptyList(),
    val settings: MapSettings = MapSettings(),
    val markersState: Loadable<List<MapMarker>> = Loadable.Idle,
)

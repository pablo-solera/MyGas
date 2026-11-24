package org.pablo.mygas.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.pablo.mygas.domain.models.map.MapCoordinate
import org.pablo.mygas.domain.models.map.MapProperties
import org.pablo.mygas.domain.models.map.MapState
import org.pablo.mygas.domain.models.map.MapViewport

/**
 * Platform map: Google Maps on Android, MapKit on iOS and an OpenStreetMap tile map on Desktop.
 *
 * [MapState.cameraPosition] is only read when the map is created; the map then owns its camera
 * and reports every settled movement through [onViewportChanged].
 */
@Composable
expect fun Map(
    modifier: Modifier = Modifier,
    state: MapState,
    properties: MapProperties = MapProperties(),
    onViewportChanged: (MapViewport) -> Unit = {},
    onMarkerClick: (String) -> Unit = {},
    onMapClick: (MapCoordinate) -> Unit = {},
)

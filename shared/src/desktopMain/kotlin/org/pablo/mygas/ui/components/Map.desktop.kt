package org.pablo.mygas.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.pablo.mygas.domain.models.map.MapCoordinate
import org.pablo.mygas.domain.models.map.MapProperties
import org.pablo.mygas.domain.models.map.MapState
import org.pablo.mygas.domain.models.map.MapViewport
import org.pablo.mygas.ui.components.tilemap.TileMap

@Composable
actual fun Map(
    modifier: Modifier,
    state: MapState,
    properties: MapProperties,
    onViewportChanged: (MapViewport) -> Unit,
    onMarkerClick: (String) -> Unit,
    onMapClick: (MapCoordinate) -> Unit,
) {
    TileMap(
        markers = state.markers,
        initialCenter = state.cameraPosition.coordinates,
        initialZoom = state.cameraPosition.zoom.toDouble(),
        modifier = modifier,
        onViewportChanged = onViewportChanged,
        onMarkerClick = onMarkerClick,
        onMapClick = onMapClick,
    )
}

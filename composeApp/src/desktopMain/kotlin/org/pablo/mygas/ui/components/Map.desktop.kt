package org.pablo.mygas.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.swmansion.kmpmaps.core.CameraPosition
import com.swmansion.kmpmaps.core.ClusterSettings
import com.swmansion.kmpmaps.core.Coordinates
import com.swmansion.kmpmaps.core.Map
import com.swmansion.kmpmaps.core.MapType
import com.swmansion.kmpmaps.core.MapUISettings
import com.swmansion.kmpmaps.core.Marker
import org.pablo.mygas.domain.models.map.MapCoordinate
import org.pablo.mygas.domain.models.map.MapProperties
import org.pablo.mygas.domain.models.map.MapState


@Composable
actual fun Map(
    modifier: Modifier,
    state: MapState,
    properties: MapProperties,
    onMapClick: (MapCoordinate) -> Unit,
    onMapLongClick: (MapCoordinate) -> Unit,
    content: @Composable (() -> Unit)
) {
    Map(
        clusterSettings = ClusterSettings(
            enabled = true,
        ),
        modifier = modifier,
        uiSettings = MapUISettings(
            compassEnabled = true,
            myLocationButtonEnabled = true
        ),
        properties = com.swmansion.kmpmaps.core.MapProperties(
            mapType = MapType.NORMAL,
            isBuildingEnabled = properties.isBuildingEnabled,
            isMyLocationEnabled = properties.isMyLocationEnabled,
            isTrafficEnabled = properties.isTrafficEnabled,
        ),
        cameraPosition = CameraPosition(
            coordinates = Coordinates(
                latitude = state.cameraPosition.coordinates.latitude,
                longitude = state.cameraPosition.coordinates.longitude,
            ),
            zoom = state.cameraPosition.zoom
        ),
        markers = state.markers.map { marker ->
            Marker(
                contentId = marker.id,
                coordinates = Coordinates(
                    latitude = marker.position.latitude,
                    longitude = marker.position.longitude
                ),
                title = marker.title,
            )
        },
        onMarkerClick = {
            println("Marker clicked: ${it.contentId}")
        },
        onMapClick = {
            onMapClick(MapCoordinate(it.latitude, it.longitude))
        },
        onMapLongClick = {
            onMapLongClick(MapCoordinate(it.latitude, it.longitude))
        },


    )
}
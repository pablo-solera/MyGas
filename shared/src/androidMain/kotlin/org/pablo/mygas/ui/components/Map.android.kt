package org.pablo.mygas.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.clustering.Clustering
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.pablo.mygas.core.utils.GasStationImage
import org.pablo.mygas.core.utils.toShortPriceText
import org.pablo.mygas.domain.models.FuelPrice
import org.pablo.mygas.domain.models.map.MapBounds
import org.pablo.mygas.domain.models.map.MapCoordinate
import org.pablo.mygas.domain.models.map.MapProperties
import org.pablo.mygas.domain.models.map.MapState
import org.pablo.mygas.domain.models.map.MapType
import org.pablo.mygas.domain.models.map.MapViewport

@Composable
private fun MapMarkerPin(
    brand: String,
    fuelPrice: FuelPrice?,
    isSelected: Boolean,
) {
    val price = fuelPrice?.price.toShortPriceText()
    val borderWidth = if (isSelected) 2.dp else 1.dp
    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }

    val pinShape = GenericShape { size, _ ->
        val tipHeight = size.height * 0.22f
        val bodyBottom = size.height - tipHeight
        val radius = bodyBottom / 2f
        val tipHalfWidth = size.width * 0.11f
        val centerX = size.width / 2f

        val path = Path().apply {
            addRoundRect(
                RoundRect(
                    left = 0f,
                    top = 0f,
                    right = size.width,
                    bottom = bodyBottom,
                    topLeftCornerRadius = CornerRadius(radius, radius),
                    topRightCornerRadius = CornerRadius(radius, radius),
                    bottomLeftCornerRadius = CornerRadius(radius, radius),
                    bottomRightCornerRadius = CornerRadius(radius, radius)
                )
            )
            moveTo(centerX - tipHalfWidth, bodyBottom)
            lineTo(centerX, size.height)
            lineTo(centerX + tipHalfWidth, bodyBottom)
            close()
        }

        addPath(path)
    }

    Surface(
        shape = pinShape,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(width = borderWidth, color = borderColor),
        tonalElevation = 2.dp,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = 36.dp)
                .padding(start = 10.dp, top = 6.dp, end = 10.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = painterResource(
                    GasStationImage.getImage(brand.substringBefore(' '))
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(18.dp)
                    .clip(RoundedCornerShape(4.dp))
            )

            Text(
                text = price,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
private fun ClusterBubble(count: Int) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shadowElevation = 4.dp,
        modifier = Modifier.size(40.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = count.toString(), style = MaterialTheme.typography.labelLarge)
        }
    }
}

class CustomMarker(
    val id: String,
    override val position: LatLng,
    override val title: String,
    override val snippet: String,
    val brand: String,
    val fuelPrice: FuelPrice?,
    val isSelected: Boolean,
) : ClusterItem {
    override val zIndex: Float? = null
}

@OptIn(MapsComposeExperimentalApi::class)
@Composable
actual fun Map(
    modifier: Modifier,
    state: MapState,
    properties: MapProperties,
    onViewportChanged: (MapViewport) -> Unit,
    onMarkerClick: (String) -> Unit,
    onMapClick: (MapCoordinate) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(state.cameraPosition.coordinates.latitude, state.cameraPosition.coordinates.longitude),
            state.cameraPosition.zoom,
        )
    }
    var mapLoaded by remember { mutableStateOf(false) }
    val currentOnViewportChanged by rememberUpdatedState(onViewportChanged)
    val currentOnMarkerClick by rememberUpdatedState(onMarkerClick)
    val currentOnMapClick by rememberUpdatedState(onMapClick)

    val clusterItems = remember(state.markers) {
        state.markers.map { marker ->
            CustomMarker(
                id = marker.id,
                position = LatLng(marker.position.latitude, marker.position.longitude),
                title = marker.title ?: marker.brand,
                snippet = "",
                brand = marker.brand,
                fuelPrice = marker.fuelPrice,
                isSelected = marker.isSelected
            )
        }
    }

    LaunchedEffect(mapLoaded, cameraPositionState) {
        if (!mapLoaded) return@LaunchedEffect
        snapshotFlow { cameraPositionState.isMoving }.collect { moving ->
            if (moving) return@collect
            val visible = cameraPositionState.projection?.visibleRegion?.latLngBounds ?: return@collect
            val camera = cameraPositionState.position
            currentOnViewportChanged(
                MapViewport(
                    center = MapCoordinate(camera.target.latitude, camera.target.longitude),
                    zoom = camera.zoom,
                    bounds = MapBounds(
                        south = visible.southwest.latitude,
                        west = visible.southwest.longitude,
                        north = visible.northeast.latitude,
                        east = visible.northeast.longitude,
                    ),
                )
            )
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = com.google.maps.android.compose.MapProperties(
            isBuildingEnabled = properties.isBuildingEnabled,
            isIndoorEnabled = properties.isIndoorEnabled,
            isMyLocationEnabled = properties.isMyLocationEnabled,
            isTrafficEnabled = properties.isTrafficEnabled,
            mapType = when (properties.mapType) {
                MapType.NORMAL -> com.google.maps.android.compose.MapType.NORMAL
                MapType.SATELLITE -> com.google.maps.android.compose.MapType.SATELLITE
                MapType.TERRAIN -> com.google.maps.android.compose.MapType.TERRAIN
                MapType.HYBRID -> com.google.maps.android.compose.MapType.HYBRID
            },
            maxZoomPreference = properties.maxZoomPreference,
            minZoomPreference = properties.minZoomPreference
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = state.settings.zoomControlsEnabled,
            compassEnabled = state.settings.compassEnabled,
            mapToolbarEnabled = state.settings.mapToolbarEnabled
        ),
        onMapLoaded = { mapLoaded = true },
        onMapClick = { currentOnMapClick(MapCoordinate(it.latitude, it.longitude)) },
    ) {
        Clustering(
            items = clusterItems,
            onClusterItemClick = { item ->
                currentOnMarkerClick(item.id)
                true
            },
            onClusterClick = { cluster ->
                val bounds = LatLngBounds.builder().apply {
                    cluster.items.forEach { include(it.position) }
                }.build()
                scope.launch {
                    cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(bounds, 120))
                }
                true
            },
            clusterItemContent = { marker ->
                MapMarkerPin(
                    brand = marker.brand,
                    fuelPrice = marker.fuelPrice,
                    isSelected = marker.isSelected
                )
            },
            clusterContent = { cluster -> ClusterBubble(cluster.size) },
        )
    }
}

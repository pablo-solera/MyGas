package org.pablo.mygas.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.clustering.Clustering
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import org.jetbrains.compose.resources.painterResource
import org.pablo.mygas.core.utils.GasStationImage
import org.pablo.mygas.domain.models.FuelPrice
import org.pablo.mygas.domain.models.map.MapCoordinate
import org.pablo.mygas.domain.models.map.MapProperties
import org.pablo.mygas.domain.models.map.MapState
import org.pablo.mygas.domain.models.map.MapType

@Composable
private fun MapMarkerPin(
    brand: String,
    fuelPrice: FuelPrice?,
    isSelected: Boolean,
) {
    val price = fuelPrice?.price?.toString() ?: "-"
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

class CustomMarker(
    private val itemPosition: LatLng,
    private val itemTitle: String,
    private val itemSnippet: String,
    val brand: String,
    val fuelPrice: FuelPrice?,
    val isSelected: Boolean,
): ClusterItem {
    override fun getPosition(): LatLng = itemPosition

    override fun getTitle(): String = itemTitle

    override fun getSnippet(): String = itemSnippet

    override fun getZIndex(): Float? = null
}

@OptIn(MapsComposeExperimentalApi::class)
@Composable
actual fun Map(
    modifier: Modifier,
    state: MapState,
    properties: MapProperties,
    onMapClick: (MapCoordinate) -> Unit,
    onMapLongClick: (MapCoordinate) -> Unit,
    content: @Composable (() -> Unit)
) {

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(
                state.cameraPosition.coordinates.latitude,
                state.cameraPosition.coordinates.longitude,
            ),
            state.cameraPosition.zoom,
        )
    }
    val adaptedMarkers = remember(state.markers) {
        state.markers.map { marker ->
            CustomMarker(
                itemPosition = LatLng(marker.position.latitude, marker.position.longitude),
                itemTitle = marker.title ?: marker.brand,
                itemSnippet = "",
                brand = marker.brand,
                fuelPrice = marker.fuelPrice,
                isSelected = marker.isSelected
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
        onMapClick = { onMapClick(MapCoordinate(it.latitude, it.longitude)) },
        onMapLongClick = { onMapLongClick(MapCoordinate(it.latitude, it.longitude)) }
    )
    {
        Clustering(
            clusterItemContent = { marker ->
                MapMarkerPin(
                    brand = marker.brand,
                    fuelPrice = marker.fuelPrice,
                    isSelected = marker.isSelected
                )
            },
            clusterContent = { cluster ->
                val firstMarker = cluster.items.firstOrNull()
                MapMarkerPin(
                    brand = firstMarker?.brand ?: "Cluster",
                    fuelPrice = firstMarker?.fuelPrice,
                    isSelected = firstMarker?.isSelected == true
                )
            },
            items = adaptedMarkers,
        )

//            state.markers.map { marker ->
//                MarkerComposable(
//                    state = rememberUpdatedMarkerState(
//                        position = LatLng(marker.position.latitude, marker.position.longitude)
//                    ),
//                    title = marker.title,
//                    anchor = Offset(0.5f, 1f)
//                ) {
//                    MapMarkerPin(
//                        brand = marker.brand,
//                        fuelPrice = marker.fuelPrice,
//                        isSelected = marker.isSelected
//                    )
//                }
//            }


    }
}

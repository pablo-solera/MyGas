package org.pablo.mygas.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import org.pablo.mygas.core.utils.toLiterPriceText
import org.pablo.mygas.domain.models.map.MapBounds
import org.pablo.mygas.domain.models.map.MapCoordinate
import org.pablo.mygas.domain.models.map.MapMarker
import org.pablo.mygas.domain.models.map.MapProperties
import org.pablo.mygas.domain.models.map.MapState
import org.pablo.mygas.domain.models.map.MapType
import org.pablo.mygas.domain.models.map.MapViewport
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKAnnotationView
import platform.MapKit.MKCoordinateRegionMake
import platform.MapKit.MKCoordinateSpanMake
import platform.MapKit.MKMapTypeHybrid
import platform.MapKit.MKMapTypeSatellite
import platform.MapKit.MKMapTypeStandard
import platform.MapKit.MKMapView
import platform.MapKit.MKMapViewDelegateProtocol
import platform.MapKit.MKPointAnnotation
import platform.darwin.NSObject
import kotlin.math.ln
import kotlin.math.pow

/**
 * MapKit implementation. NOT COMPILED OR TESTED: the project is developed on Windows, so this
 * must be verified on macOS (see AGENTS.md).
 */
@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun Map(
    modifier: Modifier,
    state: MapState,
    properties: MapProperties,
    onViewportChanged: (MapViewport) -> Unit,
    onMarkerClick: (String) -> Unit,
    onMapClick: (MapCoordinate) -> Unit,
) {
    val currentOnViewportChanged by rememberUpdatedState(onViewportChanged)
    val currentOnMarkerClick by rememberUpdatedState(onMarkerClick)
    val delegate = remember { MapDelegate() }
    delegate.onViewportChanged = currentOnViewportChanged
    delegate.onMarkerClick = currentOnMarkerClick

    UIKitView(
        modifier = modifier,
        factory = {
            MKMapView().apply {
                this.delegate = delegate
                showsCompass = state.settings.compassEnabled
                mapType = properties.mapType.toMapKit()
                val camera = state.cameraPosition
                val longitudeDelta = INITIAL_WIDTH_POINTS / TILE_POINTS * 360.0 / 2.0.pow(camera.zoom.toDouble())
                setRegion(
                    MKCoordinateRegionMake(
                        CLLocationCoordinate2DMake(camera.coordinates.latitude, camera.coordinates.longitude),
                        MKCoordinateSpanMake(longitudeDelta * ASPECT, longitudeDelta),
                    ),
                    animated = false,
                )
            }
        },
        update = { mapView ->
            mapView.mapType = properties.mapType.toMapKit()
            delegate.sync(mapView, state.markers)
        },
    )
}

private const val TILE_POINTS = 256.0
private const val INITIAL_WIDTH_POINTS = 400.0
private const val ASPECT = 1.6

private fun MapType.toMapKit() = when (this) {
    MapType.SATELLITE -> MKMapTypeSatellite
    MapType.HYBRID -> MKMapTypeHybrid
    MapType.NORMAL, MapType.TERRAIN -> MKMapTypeStandard
}

@OptIn(ExperimentalForeignApi::class)
private class MapDelegate : NSObject(), MKMapViewDelegateProtocol {
    var onViewportChanged: (MapViewport) -> Unit = {}
    var onMarkerClick: (String) -> Unit = {}

    private val annotationIds = mutableMapOf<MKPointAnnotation, String>()
    private var shownMarkers: List<MapMarker> = emptyList()

    fun sync(mapView: MKMapView, markers: List<MapMarker>) {
        if (markers == shownMarkers) return
        shownMarkers = markers
        val old = annotationIds.keys.toList()
        if (old.isNotEmpty()) mapView.removeAnnotations(old)
        annotationIds.clear()
        val created = markers.map { marker ->
            MKPointAnnotation().also { annotation ->
                annotation.setCoordinate(
                    CLLocationCoordinate2DMake(marker.position.latitude, marker.position.longitude)
                )
                annotation.setTitle(marker.fuelPrice?.price.toLiterPriceText())
                annotation.setSubtitle(marker.brand)
                annotationIds[annotation] = marker.id
            }
        }
        if (created.isNotEmpty()) mapView.addAnnotations(created)
    }

    override fun mapView(mapView: MKMapView, regionDidChangeAnimated: Boolean) {
        val widthPoints = mapView.bounds.useContents { size.width }.coerceAtLeast(1.0)
        mapView.region.useContents {
            val longitudeDelta = span.longitudeDelta.coerceAtLeast(1e-6)
            val zoom = ln(360.0 / longitudeDelta * widthPoints / TILE_POINTS) / ln(2.0)
            onViewportChanged(
                MapViewport(
                    center = MapCoordinate(center.latitude, center.longitude),
                    zoom = zoom.toFloat(),
                    bounds = MapBounds(
                        south = center.latitude - span.latitudeDelta / 2.0,
                        west = center.longitude - longitudeDelta / 2.0,
                        north = center.latitude + span.latitudeDelta / 2.0,
                        east = center.longitude + longitudeDelta / 2.0,
                    ),
                )
            )
        }
    }

    override fun mapView(mapView: MKMapView, didSelectAnnotationView: MKAnnotationView) {
        val annotation = didSelectAnnotationView.annotation as? MKPointAnnotation ?: return
        annotationIds[annotation]?.let(onMarkerClick)
        mapView.deselectAnnotation(annotation, animated = false)
    }
}

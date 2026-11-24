package org.pablo.mygas.ui.components.tilemap

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.layout.layout
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.pablo.mygas.core.map.PointCluster
import org.pablo.mygas.core.map.ScreenPoint
import org.pablo.mygas.core.map.WebMercator
import org.pablo.mygas.core.map.clusterPoints
import org.pablo.mygas.core.utils.toShortPriceText
import org.pablo.mygas.domain.models.map.MapBounds
import org.pablo.mygas.domain.models.map.MapCoordinate
import org.pablo.mygas.domain.models.map.MapMarker
import org.pablo.mygas.domain.models.map.MapViewport
import org.pablo.mygas.resources.Res
import org.pablo.mygas.resources.map_attribution
import org.pablo.mygas.resources.map_zoom_in_button
import org.pablo.mygas.resources.map_zoom_out_button
import org.pablo.mygas.ui.components.GasStationMarker
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.roundToInt

private const val MIN_ZOOM = 3.0
private const val MAX_ZOOM = 19.0
private const val CLUSTER_CELL_PX = 72f
private const val MAX_CACHED_TILES = 400

/** A draggable, zoomable OpenStreetMap tile map drawn with plain Compose. */
@OptIn(ExperimentalComposeUiApi::class, FlowPreview::class)
@Composable
fun TileMap(
    markers: List<MapMarker>,
    initialCenter: MapCoordinate,
    initialZoom: Double,
    modifier: Modifier = Modifier,
    onViewportChanged: (MapViewport) -> Unit,
    onMarkerClick: (String) -> Unit,
    onMapClick: (MapCoordinate) -> Unit,
) {
    var centerLat by remember { mutableDoubleStateOf(initialCenter.latitude) }
    var centerLon by remember { mutableDoubleStateOf(initialCenter.longitude) }
    var zoom by remember { mutableDoubleStateOf(initialZoom.coerceIn(MIN_ZOOM, MAX_ZOOM)) }
    var size by remember { mutableStateOf(IntSize.Zero) }

    val repository = remember { TileRepository() }
    val tiles = remember { mutableStateMapOf<TileKey, androidx.compose.ui.graphics.ImageBitmap>() }
    val loading = remember { mutableSetOf<TileKey>() }
    val scope = rememberCoroutineScope()
    val currentOnViewport by rememberUpdatedState(onViewportChanged)
    val currentOnMapClick by rememberUpdatedState(onMapClick)

    fun moveTo(lat: Double, lon: Double, newZoom: Double) {
        centerLat = lat.coerceIn(-85.0, 85.0)
        centerLon = lon
        zoom = newZoom.coerceIn(MIN_ZOOM, MAX_ZOOM)
    }

    /** Zooms by [delta] keeping the geographic point under [anchor] (pixels) fixed. */
    fun zoomAround(anchor: Offset, delta: Double) {
        val newZoom = (zoom + delta).coerceIn(MIN_ZOOM, MAX_ZOOM)
        if (newZoom == zoom) return
        val centerX = WebMercator.longitudeToX(centerLon, zoom)
        val centerY = WebMercator.latitudeToY(centerLat, zoom)
        val dx = anchor.x - size.width / 2.0
        val dy = anchor.y - size.height / 2.0
        val anchorLon = WebMercator.xToLongitude(centerX + dx, zoom)
        val anchorLat = WebMercator.yToLatitude(centerY + dy, zoom)
        val newCenterX = WebMercator.longitudeToX(anchorLon, newZoom) - dx
        val newCenterY = WebMercator.latitudeToY(anchorLat, newZoom) - dy
        moveTo(
            lat = WebMercator.yToLatitude(newCenterY, newZoom),
            lon = WebMercator.xToLongitude(newCenterX, newZoom),
            newZoom = newZoom,
        )
    }

    LaunchedEffect(Unit) {
        snapshotFlow { listOf(centerLat, centerLon, zoom, size.width.toDouble(), size.height.toDouble()) }
            .debounce(400)
            .collect {
                if (size.width == 0 || size.height == 0) return@collect
                val cx = WebMercator.longitudeToX(centerLon, zoom)
                val cy = WebMercator.latitudeToY(centerLat, zoom)
                val halfW = size.width / 2.0
                val halfH = size.height / 2.0
                currentOnViewport(
                    MapViewport(
                        center = MapCoordinate(centerLat, centerLon),
                        zoom = zoom.toFloat(),
                        bounds = MapBounds(
                            south = WebMercator.yToLatitude(cy + halfH, zoom),
                            west = WebMercator.xToLongitude(cx - halfW, zoom),
                            north = WebMercator.yToLatitude(cy - halfH, zoom),
                            east = WebMercator.xToLongitude(cx + halfW, zoom),
                        ),
                    )
                )
            }
    }

    val tileZoom = floor(zoom).toInt().coerceIn(0, MAX_ZOOM.toInt())
    val scale = 2.0.pow(zoom - tileZoom).toFloat()
    val tileCount = 1 shl tileZoom
    val tileCenterX = WebMercator.longitudeToX(centerLon, tileZoom.toDouble())
    val tileCenterY = WebMercator.latitudeToY(centerLat, tileZoom.toDouble())
    val tileSize = WebMercator.TILE_SIZE
    val visibleKeys = if (size == IntSize.Zero) emptyList() else {
        val halfW = size.width / 2.0 / scale
        val halfH = size.height / 2.0 / scale
        val minX = floor((tileCenterX - halfW) / tileSize).toInt().coerceAtLeast(0)
        val maxX = floor((tileCenterX + halfW) / tileSize).toInt().coerceAtMost(tileCount - 1)
        val minY = floor((tileCenterY - halfH) / tileSize).toInt().coerceAtLeast(0)
        val maxY = floor((tileCenterY + halfH) / tileSize).toInt().coerceAtMost(tileCount - 1)
        buildList {
            for (x in minX..maxX) for (y in minY..maxY) add(TileKey(tileZoom, x, y))
        }
    }

    LaunchedEffect(visibleKeys) {
        visibleKeys.filter { it !in tiles && loading.add(it) }.forEach { key ->
            scope.launch {
                repository.load(key)?.let { bitmap ->
                    if (tiles.size >= MAX_CACHED_TILES) {
                        tiles.keys.firstOrNull { it !in visibleKeys }?.let(tiles::remove)
                    }
                    tiles[key] = bitmap
                }
                loading.remove(key)
            }
        }
    }

    val background = MaterialTheme.colorScheme.surfaceVariant
    Box(
        modifier = modifier
            .onSizeChanged { size = it }
            .clipToBounds()
            .background(background)
            .onPointerEvent(PointerEventType.Scroll) { event ->
                val change = event.changes.firstOrNull() ?: return@onPointerEvent
                zoomAround(change.position, -change.scrollDelta.y * 0.35)
                change.consume()
            }
            .pointerInput(Unit) {
                detectDragGestures { change, drag ->
                    change.consume()
                    val cx = WebMercator.longitudeToX(centerLon, zoom) - drag.x
                    val cy = WebMercator.latitudeToY(centerLat, zoom) - drag.y
                    moveTo(
                        lat = WebMercator.yToLatitude(cy, zoom),
                        lon = WebMercator.xToLongitude(cx, zoom),
                        newZoom = zoom,
                    )
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { zoomAround(it, 1.0) },
                    onTap = { position ->
                        val cx = WebMercator.longitudeToX(centerLon, zoom) + (position.x - size.width / 2.0)
                        val cy = WebMercator.latitudeToY(centerLat, zoom) + (position.y - size.height / 2.0)
                        currentOnMapClick(
                            MapCoordinate(
                                WebMercator.yToLatitude(cy, zoom),
                                WebMercator.xToLongitude(cx, zoom),
                            )
                        )
                    },
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val tilePx = (tileSize * scale).toFloat()
            drawIntoCanvas { canvas ->
                visibleKeys.forEach { key ->
                    val bitmap = tiles[key] ?: return@forEach
                    val left = size.width / 2f + ((key.x * tileSize - tileCenterX) * scale).toFloat()
                    val top = size.height / 2f + ((key.y * tileSize - tileCenterY) * scale).toFloat()
                    drawImage(
                        image = bitmap,
                        dstOffset = androidx.compose.ui.unit.IntOffset(left.roundToInt(), top.roundToInt()),
                        dstSize = IntSize(tilePx.roundToInt() + 1, tilePx.roundToInt() + 1),
                    )
                }
            }
        }

        MarkerLayer(
            markers = markers,
            centerLat = centerLat,
            centerLon = centerLon,
            zoom = zoom,
            size = size,
            onMarkerClick = onMarkerClick,
            onClusterClick = { cluster ->
                val lat = WebMercator.yToLatitude(cluster.y.toDouble() + pixelCenterY(centerLat, zoom) - size.height / 2.0, zoom)
                val lon = WebMercator.xToLongitude(cluster.x.toDouble() + pixelCenterX(centerLon, zoom) - size.width / 2.0, zoom)
                moveTo(lat, lon, zoom + 2.0)
            },
        )

        Column(
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilledTonalIconButton(onClick = { zoomAround(Offset(size.width / 2f, size.height / 2f), 1.0) }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(Res.string.map_zoom_in_button))
            }
            FilledTonalIconButton(onClick = { zoomAround(Offset(size.width / 2f, size.height / 2f), -1.0) }) {
                Icon(Icons.Default.Remove, contentDescription = stringResource(Res.string.map_zoom_out_button))
            }
        }

        Surface(
            modifier = Modifier.align(Alignment.BottomStart).padding(8.dp),
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
        ) {
            Text(
                text = stringResource(Res.string.map_attribution),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            )
        }
    }
}

private fun pixelCenterX(longitude: Double, zoom: Double) = WebMercator.longitudeToX(longitude, zoom)
private fun pixelCenterY(latitude: Double, zoom: Double) = WebMercator.latitudeToY(latitude, zoom)

@Composable
private fun MarkerLayer(
    markers: List<MapMarker>,
    centerLat: Double,
    centerLon: Double,
    zoom: Double,
    size: IntSize,
    onMarkerClick: (String) -> Unit,
    onClusterClick: (PointCluster) -> Unit,
) {
    if (size == IntSize.Zero) return
    val centerX = WebMercator.longitudeToX(centerLon, zoom)
    val centerY = WebMercator.latitudeToY(centerLat, zoom)
    val margin = 80f
    val markersById = remember(markers) { markers.associateBy { it.id } }
    val points = markers.mapNotNull { marker ->
        val x = (WebMercator.longitudeToX(marker.position.longitude, zoom) - centerX + size.width / 2.0).toFloat()
        val y = (WebMercator.latitudeToY(marker.position.latitude, zoom) - centerY + size.height / 2.0).toFloat()
        if (x < -margin || y < -margin || x > size.width + margin || y > size.height + margin) null
        else ScreenPoint(marker.id, x, y)
    }
    val clusters = clusterPoints(points, CLUSTER_CELL_PX)

    Box(modifier = Modifier.fillMaxSize()) {
        clusters.forEach { cluster ->
            val single = cluster.members.singleOrNull()?.let { markersById[it.id] }
            key(single?.id ?: "cluster-${cluster.members.first().id}") {
                Box(
                    modifier = Modifier.anchoredAt(cluster.x, cluster.y, bottomCenter = single != null)
                ) {
                    if (single != null) {
                        GasStationMarker(
                            brand = single.brand,
                            price = single.fuelPrice?.price.toShortPriceText(),
                            isSelected = single.isSelected,
                            modifier = Modifier.pointerInput(single.id) {
                                detectTapGestures { onMarkerClick(single.id) }
                            },
                        )
                    } else {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            shadowElevation = 4.dp,
                            modifier = Modifier
                                .size(36.dp)
                                .pointerInput(cluster.members.size) {
                                    detectTapGestures { onClusterClick(cluster) }
                                },
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = cluster.members.size.toString(),
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Places the content so that its bottom centre (pins) or centre (clusters) is at ([x], [y]). */
private fun Modifier.anchoredAt(x: Float, y: Float, bottomCenter: Boolean): Modifier =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints.copy(minWidth = 0, minHeight = 0))
        layout(0, 0) {
            val offsetY = if (bottomCenter) placeable.height else placeable.height / 2
            placeable.place(IntOffset(x.roundToInt() - placeable.width / 2, y.roundToInt() - offsetY))
        }
    }

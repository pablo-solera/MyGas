package org.pablo.mygas.domain.models.map

import kotlin.math.abs

/** The area of the world currently visible in a map. */
data class MapViewport(
    val center: MapCoordinate,
    val zoom: Float,
    val bounds: MapBounds,
) {
    /**
     * True when the user has panned or zoomed far enough from [previous] that a new
     * "search in this area" makes sense.
     */
    fun differsSignificantlyFrom(previous: MapViewport): Boolean {
        if (abs(zoom - previous.zoom) >= ZOOM_THRESHOLD) return true
        val latitudeShift = abs(center.latitude - previous.center.latitude)
        val longitudeShift = abs(center.longitude - previous.center.longitude)
        return latitudeShift > previous.bounds.latitudeSpan * MOVE_THRESHOLD ||
            longitudeShift > previous.bounds.longitudeSpan * MOVE_THRESHOLD
    }

    private companion object {
        const val ZOOM_THRESHOLD = 0.75f
        const val MOVE_THRESHOLD = 0.2
    }
}

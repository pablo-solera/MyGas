package org.pablo.mygas.domain.models.map

data class MapBounds(
    val south: Double,
    val west: Double,
    val north: Double,
    val east: Double,
) {
    val centerLatitude: Double get() = (south + north) / 2.0
    val centerLongitude: Double get() = (west + east) / 2.0
    val latitudeSpan: Double get() = north - south
    val longitudeSpan: Double get() = east - west

    fun contains(latitude: Double, longitude: Double): Boolean =
        latitude in south..north && longitude in west..east
}

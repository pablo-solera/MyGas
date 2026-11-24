package org.pablo.mygas.core.map

import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sin

/** Web Mercator (EPSG:3857) helpers shared by slippy-map tile maps. World pixels use 256px tiles. */
object WebMercator {
    const val TILE_SIZE = 256.0
    private const val MAX_LATITUDE = 85.05112878

    fun worldSize(zoom: Double): Double = TILE_SIZE * 2.0.pow(zoom)

    fun longitudeToX(longitude: Double, zoom: Double): Double =
        (longitude + 180.0) / 360.0 * worldSize(zoom)

    fun latitudeToY(latitude: Double, zoom: Double): Double {
        val sine = sin(latitude.coerceIn(-MAX_LATITUDE, MAX_LATITUDE) * PI / 180.0)
        return (0.5 - ln((1.0 + sine) / (1.0 - sine)) / (4.0 * PI)) * worldSize(zoom)
    }

    fun xToLongitude(x: Double, zoom: Double): Double = x / worldSize(zoom) * 360.0 - 180.0

    fun yToLatitude(y: Double, zoom: Double): Double {
        val n = PI - 2.0 * PI * y / worldSize(zoom)
        return 180.0 / PI * atan(0.5 * (exp(n) - exp(-n)))
    }
}

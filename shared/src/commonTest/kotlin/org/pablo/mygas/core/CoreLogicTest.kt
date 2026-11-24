package org.pablo.mygas.core

import org.pablo.mygas.core.map.ScreenPoint
import org.pablo.mygas.core.map.WebMercator
import org.pablo.mygas.core.map.clusterPoints
import org.pablo.mygas.core.utils.formatDecimal
import org.pablo.mygas.core.utils.toEuroText
import org.pablo.mygas.core.utils.toLiterPriceText
import org.pablo.mygas.core.utils.toShortPriceText
import org.pablo.mygas.domain.models.map.MapBounds
import org.pablo.mygas.domain.models.map.MapCoordinate
import org.pablo.mygas.domain.models.map.MapViewport
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PriceFormatTest {
    @Test
    fun `decimals are rounded and padded with a comma`() {
        assertEquals("1,459", formatDecimal(1.459, 3))
        assertEquals("1,500", formatDecimal(1.5, 3))
        assertEquals("71,40", formatDecimal(71.39999999999999, 2))
        assertEquals("0,05", formatDecimal(0.05, 2))
        assertEquals("52", formatDecimal(52.4, 0))
        assertEquals("-1,50", formatDecimal(-1.5, 2))
    }

    @Test
    fun `price texts`() {
        assertEquals("1,459 €/l", 1.459.toLiterPriceText())
        assertEquals("-- €/l", null.toLiterPriceText())
        assertEquals("-- €/l", 0.0.toLiterPriceText())
        assertEquals("1,459", 1.459.toShortPriceText())
        assertEquals("--", null.toShortPriceText())
        assertEquals("72,95 €", 72.95.toEuroText())
    }
}

class WebMercatorTest {
    @Test
    fun `world edges and center project to the expected pixels`() {
        assertEquals(0.0, WebMercator.longitudeToX(-180.0, 0.0), 1e-9)
        assertEquals(128.0, WebMercator.longitudeToX(0.0, 0.0), 1e-9)
        assertEquals(256.0, WebMercator.longitudeToX(180.0, 0.0), 1e-9)
        assertEquals(128.0, WebMercator.latitudeToY(0.0, 0.0), 1e-9)
        assertEquals(WebMercator.worldSize(3.0), 2048.0, 1e-9)
    }

    @Test
    fun `projection round trips`() {
        val lat = 40.4168
        val lon = -3.7038
        val zoom = 13.0

        val x = WebMercator.longitudeToX(lon, zoom)
        val y = WebMercator.latitudeToY(lat, zoom)

        assertEquals(lon, WebMercator.xToLongitude(x, zoom), 1e-9)
        assertEquals(lat, WebMercator.yToLatitude(y, zoom), 1e-9)
    }

    @Test
    fun `north is above south`() {
        assertTrue(WebMercator.latitudeToY(41.0, 10.0) < WebMercator.latitudeToY(40.0, 10.0))
    }
}

class MarkerClusteringTest {
    @Test
    fun `points in the same cell are merged into one cluster at their centroid`() {
        val clusters = clusterPoints(
            listOf(ScreenPoint("a", 10f, 10f), ScreenPoint("b", 30f, 50f), ScreenPoint("c", 500f, 500f)),
            cellSize = 72f,
        )

        assertEquals(2, clusters.size)
        val merged = clusters.single { it.members.size == 2 }
        assertEquals(20f, merged.x)
        assertEquals(30f, merged.y)
        assertTrue(clusters.single { it.members.size == 1 }.isSingle)
    }

    @Test
    fun `no points produce no clusters`() {
        assertTrue(clusterPoints(emptyList(), 72f).isEmpty())
    }
}

class MapViewportTest {
    private fun viewport(lat: Double = 40.0, lon: Double = -3.0, zoom: Float = 13f) = MapViewport(
        center = MapCoordinate(lat, lon),
        zoom = zoom,
        bounds = MapBounds(lat - 0.05, lon - 0.05, lat + 0.05, lon + 0.05),
    )

    @Test
    fun `bounds contain only inner points`() {
        val bounds = viewport().bounds

        assertTrue(bounds.contains(40.0, -3.0))
        assertFalse(bounds.contains(41.0, -3.0))
        assertFalse(bounds.contains(40.0, -2.0))
    }

    @Test
    fun `a large pan or a zoom change is significant`() {
        val previous = viewport()

        assertTrue(viewport(lon = -2.95).differsSignificantlyFrom(previous))
        assertTrue(viewport(zoom = 14f).differsSignificantlyFrom(previous))
        assertFalse(viewport(lon = -2.995).differsSignificantlyFrom(previous))
        assertFalse(viewport(zoom = 13.3f).differsSignificantlyFrom(previous))
    }
}

package org.pablo.mygas.data

import org.pablo.mygas.data.remote.GovernmentGasStationResponse
import org.pablo.mygas.data.remote.responses.PrecioilGasStationResponse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MappersTest {

    @Test
    fun `decimal comma and thousands separator are parsed`() {
        assertEquals(1.459, "1,459".toSafeDouble())
        assertEquals(1.459, "1.459".toSafeDouble())
        assertEquals(1459.5, "1.459,5".toSafeDouble())
        assertEquals(-3.7, " -3,7 ".toSafeDouble())
    }

    @Test
    fun `blank or invalid numbers are null`() {
        assertNull("".toSafeDouble())
        assertNull("   ".toSafeDouble())
        assertNull(null.toSafeDouble())
        assertNull("abc".toSafeDouble())
        assertNull("NaN".toSafeDouble())
    }

    @Test
    fun `government station maps prices and keeps unknown fuels empty`() {
        val station = govStation().toDomainOrNull()

        assertNotNull(station)
        assertEquals(1.459, station.fuels.gasoline95.price)
        assertEquals(1.389, station.fuels.diesel.price)
        assertNull(station.fuels.gasoline95.averagePrice)
        assertNull(station.fuels.hvo.price)
        assertNull(station.fuels.dieselPremium.price)
        assertEquals(40.4168, station.location.latitude)
        assertEquals(-3.7038, station.location.longitude)
    }

    @Test
    fun `gasoline 95 falls back to the E10 price`() {
        val station = govStation(priceGasoline95E5 = null, priceGasoline95E10 = "1,419").toDomainOrNull()

        assertEquals(1.419, station?.fuels?.gasoline95?.price)
    }

    @Test
    fun `station with a non numeric id is skipped instead of failing the whole list`() {
        val stations = listOf(govStation(stationId = "x"), govStation(stationId = "12")).toDomain()

        assertEquals(listOf(12), stations.map { it.id })
    }

    @Test
    fun `station without coordinates is kept but reports an invalid location`() {
        val station = govStation(latitude = "", longitude = "").toDomainOrNull()

        assertNotNull(station)
        assertTrue(!station.hasValidLocation)
    }

    @Test
    fun `precioil biodiesel keeps the average and has no price`() {
        val station = precioil(dieselBMedia = "1,500").toDomain()

        assertNull(station.fuels.biodiesel.price)
        assertEquals(1.5, station.fuels.biodiesel.averagePrice)
    }

    private fun govStation(
        stationId: String = "1",
        latitude: String = "40,4168",
        longitude: String = "-3,7038",
        priceGasoline95E5: String? = "1,459",
        priceGasoline95E10: String? = null,
    ) = GovernmentGasStationResponse(
        stationId = stationId,
        label = "REPSOL",
        latitude = latitude,
        longitude = longitude,
        priceGasoline95E5 = priceGasoline95E5,
        priceGasoline95E10 = priceGasoline95E10,
        priceDiesel = "1,389",
    )

    private fun precioil(dieselBMedia: String? = null) = PrecioilGasStationResponse(
        idEstacion = 1,
        idMunicipio = 1,
        dieselBMedia = dieselBMedia,
    )
}

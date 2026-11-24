package org.pablo.mygas.data

import org.pablo.mygas.data.local.database.models.GasStationEntity
import org.pablo.mygas.data.remote.GovernmentGasStationResponse
import org.pablo.mygas.data.remote.responses.PrecioilGasStationResponse
import org.pablo.mygas.domain.models.FuelPrice
import org.pablo.mygas.domain.models.FuelPrices
import org.pablo.mygas.domain.models.GasStation
import org.pablo.mygas.domain.models.GeoLocation

fun PrecioilGasStationResponse.toDomain(): GasStation {
    return GasStation(
        id = idEstacion,
        name = nombreEstacion,
        brand = marca,
        address = direccion,
        postalCode = codPostal,
        town = localidad,
        province = provincia,
        location = GeoLocation(
            latitude = latitud.toSafeDouble() ?: 0.0,
            longitude = longitud.toSafeDouble() ?: 0.0
        ),
        openingHours = horario,
        lastUpdate = lastUpdate,
        fuels = FuelPrices(
            gasoline95 = FuelPrice(gasolina95.toSafeDouble(), gasolina95Media.toSafeDouble()),
            gasoline98 = FuelPrice(gasolina98.toSafeDouble(), gasolina98Media.toSafeDouble()),
            diesel = FuelPrice(diesel.toSafeDouble(), dieselMedia.toSafeDouble()),
            dieselPremium = FuelPrice(dieselPremium.toSafeDouble(), dieselPremiumMedia.toSafeDouble()),
            biodiesel = FuelPrice(price = null, averagePrice = dieselBMedia.toSafeDouble()),
            glp = FuelPrice(glp.toSafeDouble(), glpMedia.toSafeDouble()),
            hvo = FuelPrice(hvo.toSafeDouble(), hvoMedia.toSafeDouble()),
        ),
    )
}

fun GovernmentGasStationResponse.toDomainOrNull(): GasStation? {
    val id = stationId.trim().toIntOrNull() ?: return null
    return GasStation(
        id = id,
        name = label,
        brand = label,
        address = address,
        postalCode = postalCode,
        town = locality,
        province = province,
        location = GeoLocation(
            latitude = latitude.toSafeDouble() ?: 0.0,
            longitude = longitude.toSafeDouble() ?: 0.0
        ),
        openingHours = openingHours,
        lastUpdate = "",
        fuels = FuelPrices(
            gasoline95 = FuelPrice(priceGasoline95E5.toSafeDouble() ?: priceGasoline95E10.toSafeDouble(), null),
            gasoline98 = FuelPrice(priceGasoline98E5.toSafeDouble() ?: priceGasoline98E10.toSafeDouble(), null),
            diesel = FuelPrice(priceDiesel.toSafeDouble(), null),
            dieselPremium = FuelPrice(priceDieselPremium.toSafeDouble(), null),
            biodiesel = FuelPrice(priceBiodiesel.toSafeDouble(), null),
            glp = FuelPrice(priceGlp.toSafeDouble(), null),
            hvo = FuelPrice.EMPTY,
        ),
    )
}

fun List<GovernmentGasStationResponse>.toDomain(): List<GasStation> = mapNotNull { it.toDomainOrNull() }

fun GasStation.toEntity(): GasStationEntity {
    return GasStationEntity(
        id = id,
    )
}

/**
 * Parses numbers coming from the Spanish ministry: decimal comma, and optionally
 * a dot as thousands separator ("1.459,5"). Returns null for blank or invalid values.
 */
fun String?.toSafeDouble(): Double? {
    val trimmed = this?.trim()?.takeIf { it.isNotEmpty() } ?: return null
    val normalized = if (',' in trimmed) {
        trimmed.replace(".", "").replace(',', '.')
    } else {
        trimmed
    }
    return normalized.toDoubleOrNull()?.takeIf { it.isFinite() }
}

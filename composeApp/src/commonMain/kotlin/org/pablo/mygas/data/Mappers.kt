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
            gasoline95 = FuelPrice(
                price = gasolina95.toSafeDouble(),
                averagePrice = gasolina95Media.toSafeDouble()
            ),
            gasoline98 = FuelPrice(
                price = gasolina98.toSafeDouble(),
                averagePrice = gasolina98Media.toSafeDouble()
            ),
            diesel = FuelPrice(
                price = diesel.toSafeDouble(),
                averagePrice = dieselMedia.toSafeDouble()
            ),
            dieselPremium = FuelPrice(
                price = dieselPremium.toSafeDouble(),
                averagePrice = dieselPremiumMedia.toSafeDouble()
            ),
            biodiesel = FuelPrice(
                averagePrice = dieselBMedia.toSafeDouble(),
                price = dieselBMedia.toSafeDouble()
            ),
            glp = FuelPrice(
                averagePrice = glpMedia.toSafeDouble(),
                price = glp.toSafeDouble()
            ),
            hvo = FuelPrice(
                averagePrice = hvoMedia.toSafeDouble(),
                price = hvo.toSafeDouble()
            )
        ),
    )
}

fun GovernmentGasStationResponse.toDomain(): GasStation {
    return GasStation(
        id = IDEESS.toInt(),
        name = rotulo,
        brand = rotulo,
        address = Dirección,
        postalCode = cp,
        town = Municipio,
        province = Provincia,
        location = GeoLocation(
            latitude = Latitud.toSafeDouble() ?: 0.0,
            longitude = longitud.toSafeDouble() ?: 0.0
        ),
        openingHours = Horario,
        lastUpdate = "",
        fuels = FuelPrices(
            gasoline95 = FuelPrice(
                price = precioGasolina95E5.toSafeDouble(),
                averagePrice = precioGasolina95E5.toSafeDouble()
            ),
            gasoline98 = FuelPrice(
                price = 0.0,
                averagePrice = 0.0
            ),
            diesel = FuelPrice(
                price = precioGasoleoA.toSafeDouble(),
                averagePrice = precioGasoleoA.toSafeDouble()
            ),
            dieselPremium = FuelPrice(
                price = 0.0,
                averagePrice = 0.0
            ),
            biodiesel = FuelPrice(
                price = 0.0,
                averagePrice = 0.0
            ),
            glp = FuelPrice(
                price = 0.0,
                averagePrice = 0.0
            ),
            hvo = FuelPrice(
                price = 0.0,
                averagePrice = 0.0
            )
        ),
    )
}


fun GasStationEntity.toDomain(): GasStation {
    return GasStation(
        id = id,
        name = "",
        brand = "",
        address = "",
        postalCode = "",
        town = "",
        province = "",
        location = GeoLocation(
            latitude = 0.0,
            longitude = 0.0
        ),
        openingHours = "",
        lastUpdate = "",
        fuels = FuelPrices(
            gasoline95 = FuelPrice(
                price = 0.0,
                averagePrice = 0.0
            ),
            gasoline98 = FuelPrice(
                price = 0.0,
                averagePrice = 0.0
            ),
            diesel = FuelPrice(
                price = 0.0,
                averagePrice = 0.0
            ),
            dieselPremium = FuelPrice(
                price = 0.0,
                averagePrice = 0.0
            ),
            biodiesel = FuelPrice(
                averagePrice = 0.0,
                price = 0.0
            ),
            glp = FuelPrice(
                averagePrice = 0.0,
                price = 0.0
            ),
            hvo = FuelPrice(
                averagePrice = 0.0,
                price = 0.0
            )
        )
    )
}

fun GasStation.toEntity(): GasStationEntity {
    return GasStationEntity(
        id = id,
    )
}

fun String?.toSafeDouble(): Double? {
    return this
        ?.replace(",", ".")
        ?.trim()
        ?.takeIf { it.isNotBlank() }
        ?.toDoubleOrNull()
}
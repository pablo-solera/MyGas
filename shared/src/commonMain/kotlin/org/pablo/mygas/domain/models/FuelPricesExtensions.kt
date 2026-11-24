package org.pablo.mygas.domain.models

fun FuelPrices.priceFor(fuelType: FuelType): FuelPrice = when (fuelType) {
    FuelType.GASOLINE_95 -> gasoline95
    FuelType.GASOLINE_98 -> gasoline98
    FuelType.DIESEL -> diesel
    FuelType.DIESEL_PREMIUM -> dieselPremium
    FuelType.BIO_DIESEL -> biodiesel
    FuelType.GLP -> glp
    FuelType.HVO -> hvo
}

package org.pablo.mygas.domain.models

val FuelType.displayName: String
    get() = when (this) {
        FuelType.GASOLINE_95 -> "Gasolina 95"
        FuelType.GASOLINE_98 -> "Gasolina 98"
        FuelType.DIESEL -> "Gasóleo A"
        FuelType.DIESEL_PREMIUM -> "Gasóleo premium"
        FuelType.BIO_DIESEL -> "Biodiésel"
        FuelType.GLP -> "GLP"
        FuelType.HVO -> "HVO"
    }

fun FuelPrices.priceFor(fuelType: FuelType): FuelPrice = when (fuelType) {
    FuelType.GASOLINE_95 -> gasoline95
    FuelType.GASOLINE_98 -> gasoline98
    FuelType.DIESEL -> diesel
    FuelType.DIESEL_PREMIUM -> dieselPremium
    FuelType.BIO_DIESEL -> biodiesel
    FuelType.GLP -> glp
    FuelType.HVO -> hvo
}

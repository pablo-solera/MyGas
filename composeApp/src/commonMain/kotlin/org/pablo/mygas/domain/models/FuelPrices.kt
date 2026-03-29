package org.pablo.mygas.domain.models

data class FuelPrices(
    val gasoline95: FuelPrice,
    val gasoline98: FuelPrice,
    val diesel: FuelPrice,
    val dieselPremium: FuelPrice,
    val biodiesel: FuelPrice,
    val glp: FuelPrice,
    val hvo: FuelPrice
)
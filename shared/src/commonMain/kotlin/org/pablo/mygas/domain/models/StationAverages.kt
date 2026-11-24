package org.pablo.mygas.domain.models

/** Fills each station's [FuelPrice.averagePrice] with the mean price of the given list. */
fun List<GasStation>.withAveragePrices(): List<GasStation> {
    if (isEmpty()) return this
    fun average(selector: (FuelPrices) -> FuelPrice): Double? =
        mapNotNull { selector(it.fuels).price?.takeIf { price -> price > 0.0 } }
            .takeIf { it.isNotEmpty() }
            ?.average()

    val gasoline95 = average { it.gasoline95 }
    val gasoline98 = average { it.gasoline98 }
    val diesel = average { it.diesel }
    val dieselPremium = average { it.dieselPremium }
    val biodiesel = average { it.biodiesel }
    val glp = average { it.glp }
    val hvo = average { it.hvo }
    return map { station ->
        val fuels = station.fuels
        station.copy(
            fuels = FuelPrices(
                gasoline95 = fuels.gasoline95.copy(averagePrice = gasoline95),
                gasoline98 = fuels.gasoline98.copy(averagePrice = gasoline98),
                diesel = fuels.diesel.copy(averagePrice = diesel),
                dieselPremium = fuels.dieselPremium.copy(averagePrice = dieselPremium),
                biodiesel = fuels.biodiesel.copy(averagePrice = biodiesel),
                glp = fuels.glp.copy(averagePrice = glp),
                hvo = fuels.hvo.copy(averagePrice = hvo),
            )
        )
    }
}

package org.pablo.mygas.domain.models

data class FuelPrice(
    val price: Double?,
    val averagePrice: Double?
) {
    companion object {
        val EMPTY = FuelPrice(price = null, averagePrice = null)
    }
}

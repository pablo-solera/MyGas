package org.pablo.mygas.core.utils

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.round

/** Formats [value] with a decimal comma and exactly [decimals] fraction digits (no locale APIs in common code). */
fun formatDecimal(value: Double, decimals: Int): String {
    require(decimals >= 0)
    if (!value.isFinite()) return "--"
    val factor = 10.0.pow(decimals)
    val scaled = round(abs(value) * factor).toLong()
    val divisor = factor.toLong()
    val integerPart = scaled / divisor
    val sign = if (value < 0 && scaled != 0L) "-" else ""
    if (decimals == 0) return "$sign$integerPart"
    val fraction = (scaled % divisor).toString().padStart(decimals, '0')
    return "$sign$integerPart,$fraction"
}

const val NO_PRICE = "--"

/** Price per litre, e.g. "1,459 €/l". Missing or non-positive prices are shown as "-- €/l". */
fun Double?.toLiterPriceText(): String =
    if (this == null || this <= 0.0) "$NO_PRICE €/l" else "${formatDecimal(this, 3)} €/l"

/** Short price for map pins, e.g. "1,459". */
fun Double?.toShortPriceText(): String =
    if (this == null || this <= 0.0) NO_PRICE else formatDecimal(this, 3)

/** Money amount, e.g. "72,95 €". */
fun Double.toEuroText(): String = "${formatDecimal(this, 2)} €"

package org.pablo.mygas.domain.models.map

import org.pablo.mygas.domain.models.FuelPrice

data class MapMarker(
    val id: String,
    val position: MapCoordinate,
    val title: String? = null,
    val brand: String,
    val isSelected: Boolean = false,
    val fuelPrice: FuelPrice? = null
)
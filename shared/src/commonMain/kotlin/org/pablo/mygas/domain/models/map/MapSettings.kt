package org.pablo.mygas.domain.models.map

data class MapSettings(
    val compassEnabled: Boolean = true,
    val indoorLevelPickerEnabled: Boolean = false,
    val mapToolbarEnabled: Boolean = false,
    val myLocationButtonEnabled: Boolean = true,
    val rotationGesturesEnabled: Boolean = true,
    val scrollGesturesEnabled: Boolean = true,
    val scrollGesturesEnabledDuringRotateOrZoom: Boolean = true,
    val tiltGesturesEnabled: Boolean = true,
    val zoomControlsEnabled: Boolean = false,
    val zoomGesturesEnabled: Boolean = true
)
package org.pablo.mygas.core.utils

/** Opens an external maps application (or the browser) showing a location. */
interface MapLauncher {
    fun open(latitude: Double, longitude: Double)
}

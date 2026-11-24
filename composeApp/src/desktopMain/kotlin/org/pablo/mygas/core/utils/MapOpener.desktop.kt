package org.pablo.mygas.core.utils

import java.awt.Desktop
import java.net.URI

actual class MapOpener actual constructor() {
    actual fun openMapActivity(latitude: String, longitude: String) {
        val lat = latitude.replace(",", ".").toDoubleOrNull() ?: return
        val lon = longitude.replace(",", ".").toDoubleOrNull() ?: return
        if (!lat.isFinite() || !lon.isFinite()) return
        if (!Desktop.isDesktopSupported()) return
        val desktop = Desktop.getDesktop()
        if (!desktop.isSupported(Desktop.Action.BROWSE)) return
        desktop.browse(URI("https://www.google.com/maps/search/?api=1&query=$lat,$lon"))
    }
}

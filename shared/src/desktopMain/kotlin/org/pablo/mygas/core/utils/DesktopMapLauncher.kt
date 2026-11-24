package org.pablo.mygas.core.utils

import java.awt.Desktop
import java.net.URI

class DesktopMapLauncher : MapLauncher {
    override fun open(latitude: Double, longitude: Double) {
        if (!latitude.isFinite() || !longitude.isFinite()) return
        if (!Desktop.isDesktopSupported()) return
        val desktop = Desktop.getDesktop()
        if (!desktop.isSupported(Desktop.Action.BROWSE)) return
        desktop.browse(URI("https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"))
    }
}

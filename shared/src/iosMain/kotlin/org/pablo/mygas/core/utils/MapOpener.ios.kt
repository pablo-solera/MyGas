package org.pablo.mygas.core.utils

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual class MapOpener actual constructor() {
    actual fun openMapActivity(latitude: String, longitude: String) {
        val lat = latitude.replace(",", ".").toDoubleOrNull() ?: return
        val lon = longitude.replace(",", ".").toDoubleOrNull() ?: return
        val url = NSURL.URLWithString("https://www.google.com/maps/search/?api=1&query=$lat,$lon") ?: return
        UIApplication.sharedApplication.openURL(url, options = emptyMap<Any?, Any?>(), completionHandler = null)
    }
}

package org.pablo.mygas.core.utils

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

class IosMapLauncher : MapLauncher {
    override fun open(latitude: Double, longitude: Double) {
        if (!latitude.isFinite() || !longitude.isFinite()) return
        val url = NSURL.URLWithString("https://www.google.com/maps/search/?api=1&query=$latitude,$longitude") ?: return
        UIApplication.sharedApplication.openURL(url, options = emptyMap<Any?, Any?>(), completionHandler = null)
    }
}

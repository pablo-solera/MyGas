package org.pablo.mygas.core.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import org.koin.core.context.GlobalContext

actual class MapOpener actual constructor() {
    actual fun openMapActivity(latitude: String, longitude: String) {
        val appContext = GlobalContext.get().get<Context>()

        val lat = latitude.replace(",", ".").toDoubleOrNull()
        val lon = longitude.replace(",", ".").toDoubleOrNull()
        if (lat == null || lon == null || !lat.isFinite() || !lon.isFinite()) return

        val uri = "geo:0,0?q=$lat,$lon".toUri()
        val googleMaps = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage(GOOGLE_MAPS_PACKAGE)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val generic = Intent(Intent.ACTION_VIEW, uri).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }

        try {
            appContext.startActivity(googleMaps)
        } catch (_: ActivityNotFoundException) {
            try {
                appContext.startActivity(generic)
            } catch (_: ActivityNotFoundException) {
                // No maps app installed: nothing to open.
            }
        }
    }

    private companion object {
        const val GOOGLE_MAPS_PACKAGE = "com.google.android.apps.maps"
    }
}

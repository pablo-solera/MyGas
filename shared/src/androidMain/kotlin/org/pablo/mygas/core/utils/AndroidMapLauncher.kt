package org.pablo.mygas.core.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

class AndroidMapLauncher(private val context: Context) : MapLauncher {
    override fun open(latitude: Double, longitude: Double) {
        if (!latitude.isFinite() || !longitude.isFinite()) return
        val uri = "geo:0,0?q=$latitude,$longitude".toUri()
        val googleMaps = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage(GOOGLE_MAPS_PACKAGE)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val generic = Intent(Intent.ACTION_VIEW, uri).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
        try {
            context.startActivity(googleMaps)
        } catch (_: ActivityNotFoundException) {
            try {
                context.startActivity(generic)
            } catch (_: ActivityNotFoundException) {
                // No maps app installed: nothing to open.
            }
        }
    }

    private companion object {
        const val GOOGLE_MAPS_PACKAGE = "com.google.android.apps.maps"
    }
}

package org.pablo.mygas.core.utils

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import org.koin.core.context.GlobalContext

actual class MapOpener actual constructor() {
    actual fun openMapActivity(latitude: String, longitude: String) {

        val appContext = GlobalContext.get().get<Context>()

        val latitudeFormatted = latitude.replace(",", ".").toDoubleOrNull()
        val longitudeFormatted = longitude.replace(",", ".").toDoubleOrNull()
        if (latitudeFormatted == null || longitudeFormatted == null ||
            !latitudeFormatted.isFinite() || !longitudeFormatted.isFinite()
        ) return

        val gmmIntentUri = "geo:0,0?q=$latitudeFormatted,$longitudeFormatted".toUri()
        val packageManager = appContext.packageManager
        val googleMapsIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            setPackage("com.google.android.apps.maps")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val mapIntent = if (googleMapsIntent.resolveActivity(packageManager) != null) {
            googleMapsIntent
        } else {
            Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }.takeIf { it.resolveActivity(packageManager) != null }
        }

        mapIntent?.let { appContext.startActivity(it, null) }
    }
}

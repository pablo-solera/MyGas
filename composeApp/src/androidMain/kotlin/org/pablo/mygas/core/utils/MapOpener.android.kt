package org.pablo.mygas.core.utils

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import org.koin.core.context.GlobalContext

actual class MapOpener actual constructor() {
    actual fun openMapActivity(latitude: String, longitude: String) {

        val appContext = GlobalContext.get().get<Context>()

        // Replace commas with dots to ensure the format is correct for the URI
        val latitudeFormatted = latitude.replace(",", ".")
        val longitudeFormatted = longitude.replace(",", ".")

        //val gmmIntentUri = "google.navigation:q=$latitudeFormatted,$longitudeFormatted".toUri()
        val gmmIntentUri = "geo:0,0?q=$latitudeFormatted,$longitudeFormatted".toUri()
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        appContext.startActivity(mapIntent, null)
    }
}
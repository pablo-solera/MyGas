package org.pablo.mygas.utils

import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri

actual class MapOpener actual constructor() {
    actual fun openMapActivity(latitude: String, longitude: String) {

        // Replace commas with dots to ensure the format is correct for the URI
        val latitudeFormatted = latitude.replace(",", ".")
        val longitudeFormatted = longitude.replace(",", ".")

        //val gmmIntentUri = "google.navigation:q=$latitudeFormatted,$longitudeFormatted".toUri()
        val gmmIntentUri = "geo:0,0?q=$latitudeFormatted,$longitudeFormatted".toUri()
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(AppContext.get(), mapIntent, null)

    }
}
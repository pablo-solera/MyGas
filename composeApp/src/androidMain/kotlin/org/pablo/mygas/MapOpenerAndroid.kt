package org.pablo.mygas

import android.content.Context
import android.content.Intent
import android.net.Uri
import org.pablo.mygas.utils.MapOpener

class MapOpenerAndroid(private val context: Context) : MapOpener {
    override fun openGoogleMaps(lat: String, lon: String) {
        val gmmIntentUri = Uri.parse("geo:$lat,$lon")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        context.startActivity(mapIntent)
    }
}
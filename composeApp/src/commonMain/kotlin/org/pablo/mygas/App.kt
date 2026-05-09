package org.pablo.mygas

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.swmansion.kmpmaps.core.MapConfiguration
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.pablo.mygas.core.navigation.Navigation


@Composable
@Preview
fun App() {
    LaunchedEffect(Unit) {
        MapConfiguration.initialize(googleMapsApiKey = BuildKonfig.MAPS_API_KEY)
    }

    MaterialTheme {
        Navigation()
    }
}

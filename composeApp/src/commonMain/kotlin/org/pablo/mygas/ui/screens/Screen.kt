package org.pablo.mygas.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.crossfade
import coil3.util.DebugLogger

@Composable
fun Screen (modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    MaterialTheme {

        Surface(
            modifier = modifier.fillMaxSize(),
            content = content
        )

    }
}
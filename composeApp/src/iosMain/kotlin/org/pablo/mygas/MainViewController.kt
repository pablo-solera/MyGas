package org.pablo.mygas

import androidx.compose.ui.window.ComposeUIViewController
import org.pablo.mygas.core.di.initKoin

private var koinInitialized = false

fun MainViewController() = ComposeUIViewController(
    configure = {
        if (!koinInitialized) {
            initKoin()
            koinInitialized = true
        }
    }
) { App() }

package org.pablo.mygas

import androidx.compose.ui.window.ComposeUIViewController
import org.pablo.mygas.core.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = { initKoin() }
) { App() }

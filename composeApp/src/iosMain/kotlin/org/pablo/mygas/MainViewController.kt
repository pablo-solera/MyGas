package org.pablo.mygas

import androidx.compose.ui.window.ComposeUIViewController
import org.koin.core.context.GlobalContext
import org.pablo.mygas.core.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        if (GlobalContext.getOrNull() == null) initKoin()
    }
) { App() }

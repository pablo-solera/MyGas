package org.pablo.mygas

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.pablo.mygas.core.di.initKoin

fun main() {
    initKoin()
    application {
        Window(onCloseRequest = ::exitApplication, title = "MyGas") {
            App()
        }
    }
}

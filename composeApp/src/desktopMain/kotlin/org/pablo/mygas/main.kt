package org.pablo.mygas

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.datlag.kcef.KCEF
import kotlinx.coroutines.runBlocking
import org.pablo.mygas.core.di.initKoin

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = {
                runBlocking { KCEF.dispose() }
                exitApplication()
            },
            title = "MyGas",
        ) {
            App()
        }
    }
}

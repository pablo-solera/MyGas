package org.pablo.mygas

import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.datlag.kcef.KCEF
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.pablo.mygas.core.di.initKoin
import java.io.File

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "MyGas",
        ) {
            var isInitialized by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                withContext(Dispatchers.IO) {
                    KCEF.init(
                        builder = {
                            installDir(File("kcef-bundle"))
                            progress { onInitialized { isInitialized = true } }
                            settings { noSandbox = true }
                        },
                        onError = { error -> println("KCEF error: $error") }
                    )
                }
            }
            if (isInitialized) {
                App()
            }else{
                Text("Initializing Map...")
            }
        }
    }
}

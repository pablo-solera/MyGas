package org.pablo.mygas

import androidx.compose.runtime.Composable
import org.pablo.mygas.core.navigation.Navigation
import org.pablo.mygas.ui.theme.MyGasTheme

@Composable
fun App() {
    MyGasTheme {
        Navigation()
    }
}

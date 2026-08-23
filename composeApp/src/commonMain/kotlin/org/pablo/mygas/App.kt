package org.pablo.mygas

import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.pablo.mygas.core.navigation.Navigation
import org.pablo.mygas.ui.theme.MyGasTheme


@Composable
@Preview
fun App() {
    MyGasTheme {
        Navigation()
    }
}

package org.pablo.mygas

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.pablo.mygas.core.navigation.Navigation
import org.pablo.mygas.domain.models.ThemeMode
import org.pablo.mygas.ui.theme.MyGasTheme
import org.pablo.mygas.ui.viewmodels.ThemeViewModel

@Composable
fun App(themeViewModel: ThemeViewModel = koinViewModel()) {
    val themeMode by themeViewModel.themeMode.collectAsStateWithLifecycle()
    val dark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    MyGasTheme(darkTheme = dark) {
        Navigation()
    }
}

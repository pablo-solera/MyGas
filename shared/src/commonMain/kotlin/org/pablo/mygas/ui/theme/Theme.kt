package org.pablo.mygas.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = Petrol,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = androidx.compose.ui.graphics.Color(0xFFBCEDEA),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFF002021),
    secondary = androidx.compose.ui.graphics.Color(0xFF4E6261),
    background = Paper,
    surface = Paper,
    surfaceContainer = PaperContainer,
    onBackground = Ink,
    onSurface = Ink,
    error = ErrorRed,
)

private val DarkColors = darkColorScheme(
    primary = PetrolDark,
    onPrimary = androidx.compose.ui.graphics.Color(0xFF003737),
    primaryContainer = androidx.compose.ui.graphics.Color(0xFF174849),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFFBCEDEA),
    secondary = androidx.compose.ui.graphics.Color(0xFFB3CCCA),
    background = Night,
    surface = Night,
    surfaceContainer = NightContainer,
    onBackground = androidx.compose.ui.graphics.Color(0xFFE1E3DF),
    onSurface = androidx.compose.ui.graphics.Color(0xFFE1E3DF),
    error = androidx.compose.ui.graphics.Color(0xFFFFB4AB),
)

val MyGasShapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
    small = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
)

@Composable
fun MyGasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = MyGasTypography,
        shapes = MyGasShapes,
        content = content,
    )
}

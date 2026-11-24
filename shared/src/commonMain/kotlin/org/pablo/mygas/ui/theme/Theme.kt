package org.pablo.mygas.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

// Every role is defined so no Material default (purple) leaks into surfaces, chips or outlines.
private val LightColors = lightColorScheme(
    primary = Petrol,
    onPrimary = White,
    primaryContainer = PetrolContainer,
    onPrimaryContainer = OnPetrolContainer,
    secondary = Slate,
    onSecondary = White,
    secondaryContainer = SlateContainer,
    onSecondaryContainer = OnSlateContainer,
    tertiary = Amber,
    onTertiary = White,
    tertiaryContainer = AmberContainer,
    onTertiaryContainer = OnAmberContainer,
    background = Paper,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    surfaceVariant = PaperVariant,
    onSurfaceVariant = InkVariant,
    surfaceContainerLowest = White,
    surfaceContainerLow = PaperLow,
    surfaceContainer = PaperContainer,
    surfaceContainerHigh = PaperHigh,
    surfaceContainerHighest = PaperHighest,
    outline = Outline,
    outlineVariant = OutlineVariant,
    error = ErrorRed,
    onError = White,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
)

private val DarkColors = darkColorScheme(
    primary = PetrolDark,
    onPrimary = OnPetrolDark,
    primaryContainer = PetrolContainerDark,
    onPrimaryContainer = PetrolOnContainerDark,
    secondary = SlateDark,
    onSecondary = OnSlateDark,
    secondaryContainer = SlateContainerDark,
    onSecondaryContainer = SlateOnContainerDark,
    tertiary = AmberDark,
    onTertiary = OnAmberDark,
    tertiaryContainer = AmberContainerDark,
    onTertiaryContainer = AmberOnContainerDark,
    background = Night,
    onBackground = Mist,
    surface = Night,
    onSurface = Mist,
    surfaceVariant = NightVariant,
    onSurfaceVariant = MistVariant,
    surfaceContainerLowest = NightLowest,
    surfaceContainerLow = NightLow,
    surfaceContainer = NightContainer,
    surfaceContainerHigh = NightHigh,
    surfaceContainerHighest = NightHighest,
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark,
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
)

val MyGasShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
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

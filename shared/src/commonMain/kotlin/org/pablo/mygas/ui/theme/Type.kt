package org.pablo.mygas.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight

val MyGasTypography = Typography().run {
    copy(
        headlineMedium = headlineMedium.copy(fontWeight = FontWeight.SemiBold),
        titleLarge = titleLarge.copy(fontWeight = FontWeight.SemiBold),
        titleMedium = titleMedium.copy(fontWeight = FontWeight.SemiBold),
        labelLarge = labelLarge.copy(fontWeight = FontWeight.SemiBold),
    )
}

object MyGasSpacing {
    val xs = androidx.compose.ui.unit.Dp(4f)
    val sm = androidx.compose.ui.unit.Dp(8f)
    val md = androidx.compose.ui.unit.Dp(16f)
    val lg = androidx.compose.ui.unit.Dp(24f)
    val xl = androidx.compose.ui.unit.Dp(32f)
}

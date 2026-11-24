package org.pablo.mygas.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.pablo.mygas.domain.models.map.MapCoordinate
import org.pablo.mygas.domain.models.map.MapProperties
import org.pablo.mygas.domain.models.map.MapState

@Composable
actual fun Map(
    modifier: Modifier,
    state: MapState,
    properties: MapProperties,
    onMapClick: (MapCoordinate) -> Unit,
    onMapLongClick: (MapCoordinate) -> Unit,
    content: @Composable () -> Unit,
) = Unit

package org.pablo.mygas.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GasStationSkeletonList(
    modifier: Modifier = Modifier,
    itemCount: Int = 6,
) {
    val transition = rememberInfiniteTransition(label = "station-list-shimmer")
    val shimmerOffset = transition.animateFloat(
        initialValue = -450f,
        targetValue = 1_200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1_200),
            repeatMode = RepeatMode.Restart
        ),
        label = "station-list-shimmer-offset"
    )

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(count = itemCount) {
            GasStationItemSkeleton(shimmerOffset = shimmerOffset)
        }
    }
}

package org.pablo.mygas.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun GasStationItemSkeleton(
    modifier: Modifier = Modifier,
    shimmerOffset: State<Float>,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShimmerBox(
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    shimmerOffset = shimmerOffset
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth(0.62f)
                            .height(18.dp),
                        shape = RoundedCornerShape(8.dp),
                        shimmerOffset = shimmerOffset
                    )
                    ShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(14.dp),
                        shape = RoundedCornerShape(8.dp),
                        shimmerOffset = shimmerOffset
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(3) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ShimmerBox(
                            modifier = Modifier
                                .fillMaxWidth(0.72f)
                                .height(12.dp),
                            shape = RoundedCornerShape(8.dp),
                            shimmerOffset = shimmerOffset
                        )
                        ShimmerBox(
                            modifier = Modifier
                                .fillMaxWidth(0.84f)
                                .height(16.dp),
                            shape = RoundedCornerShape(8.dp),
                            shimmerOffset = shimmerOffset
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShimmerBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
    shimmerOffset: State<Float>,
) {
    val baseColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
    val highlightColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f)

    Box(
        modifier = modifier
            .clip(shape)
            .drawWithCache {
                onDrawBehind {
                    val offset = shimmerOffset.value
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(baseColor, highlightColor, baseColor),
                            start = Offset(offset, 0f),
                            end = Offset(offset + 450f, 450f)
                        )
                    )
                }
            }
    )
}

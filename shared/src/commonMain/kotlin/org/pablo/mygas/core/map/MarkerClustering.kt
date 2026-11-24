package org.pablo.mygas.core.map

import kotlin.math.floor

data class ScreenPoint(val id: String, val x: Float, val y: Float)

data class PointCluster(val x: Float, val y: Float, val members: List<ScreenPoint>) {
    val isSingle: Boolean get() = members.size == 1
}

/** Groups points that fall in the same square cell of [cellSize] pixels (simple grid clustering). */
fun clusterPoints(points: List<ScreenPoint>, cellSize: Float): List<PointCluster> {
    require(cellSize > 0f)
    return points
        .groupBy { floor(it.x / cellSize).toInt() to floor(it.y / cellSize).toInt() }
        .values
        .map { group ->
            PointCluster(
                x = group.sumOf { it.x.toDouble() }.toFloat() / group.size,
                y = group.sumOf { it.y.toDouble() }.toFloat() / group.size,
                members = group,
            )
        }
}
